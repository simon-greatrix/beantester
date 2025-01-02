package io.setl.beantester;

import java.time.Clock;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

import lombok.Getter;
import lombok.Setter;

import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.factories.time.RandomClock;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanDescriptionLookup;
import io.setl.beantester.info.Specs;
import io.setl.beantester.info.Specs.Spec;

/**
 * A class that holds the test context. This is to support multi-threading during testing.
 */
public class TestContext {

  private static final TestContext DEFAULT;

  /** A magic value that indicates the seed has not been set. The value is the digits of the 3*sqrt(5). */
  private static final long MAGIC_SEED_NOT_SET = 2236067977499789696L * 3;

  private static final RandomGeneratorFactory<?> randomFactory;

  private static final ThreadLocal<TestContext> CONTEXT = ThreadLocal.withInitial(
      () -> {
        TestContext context = new TestContext();
        context.loadDefaults();
        return context;
      }
  );

  private static final SplittableGenerator root;


  public static void close() {
    CONTEXT.remove();
    ValueFactory.DEPTH.remove();
  }


  /**
   * Get this thread's test context.
   *
   * @return the test context
   */
  public static TestContext get() {
    return CONTEXT.get();
  }


  /**
   * Create a new random number generator.
   *
   * @return the new random number generator
   */
  public static RandomGenerator newRandom() {
    return root.split();
  }


  static {
    RandomGeneratorFactory<?> factory;
    try {
      // The Javadoc for the java.util.random package says this algorithm is good when there are no special requirements.
      factory = RandomGeneratorFactory.of("L64X128MixRandom");
    } catch (IllegalArgumentException e) {
      // L64X128MixRandom is not supported, use the one with the most state bits
      // SecureRandom has MAX_VALUE stateBits.
      factory = RandomGeneratorFactory.all()
          .filter(rgf -> !rgf.name().equals("SecureRandom")) // SecureRandom has MAX_VALUE stateBits.
          .filter(RandomGeneratorFactory::isSplittable)
          .max(Comparator.comparingInt(RandomGeneratorFactory<RandomGenerator>::stateBits))
          .orElseThrow();
    }
    randomFactory = factory;
    root = (SplittableGenerator) randomFactory.create();

    DEFAULT = new TestContext();
    CONTEXT.set(DEFAULT);
    DEFAULT.factories.loadDefaults();
    CONTEXT.remove();
  }

  /** A clock for time and date related values. */
  @Getter
  private final RandomClock clock = new RandomClock();

  /** The factories for creating values. */
  @Getter
  private final FactoryRepository factories;

  /** Lookups for bean descriptions. */
  private final LinkedList<BeanDescriptionLookup> lookups = new LinkedList<>();

  /** The maximum number of distinct instances to generate within one structure. */
  @Getter
  @Setter
  private int maxDistinctValues = 5;

  /** If a property can be set either in the constructor or by a setter, which should be preferred. */
  @Getter
  @Setter
  private boolean preferWriters = true;

  /**
   * The random number generator.
   */
  @Getter
  private RandomGenerator random;

  /** A random seed for repeatable tests. */
  private long randomSeed = MAGIC_SEED_NOT_SET;

  /** The recursion depth of the current test. */
  @Getter
  @Setter
  private int structureDepth = 10;

  private long structureId = 0;


  /** New instance. */
  private TestContext() {
    random = newRandom();
    factories = new FactoryRepository();
  }


  /**
   * Add a lookup for bean descriptions.
   *
   * @param lookup the lookup to add
   */
  public void addDescriptionLookup(BeanDescriptionLookup lookup) {
    lookups.add(lookup);
  }


  /**
   * Convenience method to add a factory to the repository.
   *
   * @param valueFactory the factory
   */
  public void addFactory(ValueFactory valueFactory) {
    getFactories().addFactory(valueFactory);
  }


  /**
   * Add a factory for a specific property.
   *
   * @param clazz        the class that has the property
   * @param propertyName the property's name
   * @param valueFactory the factory to use for just this property
   */
  public void addFactory(Class<?> clazz, String propertyName, ValueFactory valueFactory) {
    getFactories().addFactory(clazz, propertyName, valueFactory);
  }


  /**
   * Convenience method to add a factory to the repository.
   *
   * @param description a bean description from which a factory is created
   */
  public void addFactory(BeanDescription description) {
    getFactories().addFactory(description);
  }


  /**
   * Create a {@code BeanDescription} for the specified class.
   *
   * @param beanClass the bean's class
   * @param specs     additional specifiers for the bean
   *
   * @return the bean description
   */
  public BeanDescription create(Class<?> beanClass, Specs.Spec... specs) {
    return BeanDescription.create(beanClass, specs);
  }


  long getStructureId() {
    return structureId;
  }


  /**
   * Find an explicit factory for the specified type description.
   *
   * @param clazz the class to find a factory for
   * @param specs additional specifiers for the bean
   *
   * @return the factory if found
   */
  public Optional<BeanDescriptionLookup> findBeanDescriptionLookup(Class<?> clazz, Spec... specs) {
    for (BeanDescriptionLookup lookup : lookups) {
      if (lookup.hasDescription(clazz, specs)) {
        return Optional.of(lookup);
      }
    }
    return Optional.empty();
  }


  private void loadDefaults() {
    if (DEFAULT.randomSeed != MAGIC_SEED_NOT_SET) {
      setRepeatable(DEFAULT.randomSeed);
    }

    Clock delegateClock = DEFAULT.clock.getDelegate();
    if (delegateClock != null) {
      // We call "withZone" like this to create a new instance of the clock.
      clock.setDelegate(delegateClock.withZone(delegateClock.getZone()));
    }

    preferWriters = DEFAULT.preferWriters;

    factories.copy(DEFAULT.factories);

    lookups.addAll(DEFAULT.lookups);
  }


  void beginStructure() {
    structureId++;

  }


  /**
   * Set the clock to be used for time and date related values.
   *
   * @param clock the clock to use
   *
   * @return this
   */
  public TestContext setClock(Clock clock) {
    this.clock.setDelegate(clock);
    return this;
  }


  /**
   * Change the random number generator to be a repeatable one. This allows for repeatable tests.
   *
   * @param seed the seed for the random number generator
   *
   * @return this
   */
  public TestContext setRepeatable(long seed) {
    if (seed == MAGIC_SEED_NOT_SET) {
      seed++;
    }
    randomSeed = seed;
    random = randomFactory.create(seed);
    return this;
  }

}
