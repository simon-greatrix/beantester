package com.pippsford.beantester;

import java.lang.System.Logger.Level;
import java.time.Clock;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

import lombok.Getter;
import lombok.Setter;

import com.pippsford.beantester.factories.FactoryRepository;
import com.pippsford.beantester.factories.time.RandomClock;
import com.pippsford.beantester.info.BeanDescription;
import com.pippsford.beantester.info.BeanDescriptionLookup;
import com.pippsford.beantester.info.Specs.Spec;

/**
 * A class that holds the test context. This is to support multi-threading during testing.
 */
public class TestContext {

  private static final ThreadLocal<LinkedList<TestContext>> CONTEXT;

  private static final TestContext DEFAULT;

  /** A magic value that indicates the seed has not been set. The value is the digits of the 3*sqrt(5). */
  private static final long MAGIC_SEED_NOT_SET = 2236067977499789696L * 3;

  private static final RandomGeneratorFactory<?> randomFactory;

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
    return CONTEXT.get().getLast();
  }


  /**
   * Create a new random number generator.
   *
   * @return the new random number generator
   */
  public static RandomGenerator newRandom() {
    return root.split();
  }


  /**
   * Remove the current context (which must have been pushed), and return the previous context.
   *
   * @return the previous test context
   */
  public static TestContext pop() {
    LinkedList<TestContext> list = CONTEXT.get();
    if (list.size() <= 1) {
      throw new IllegalStateException("Cannot pop the last context");
    }
    CONTEXT.get().removeLast();
    return CONTEXT.get().getLast();
  }


  /**
   * Create a new context, push it onto the stack, and return the new context.
   *
   * @return the test context
   */
  public static TestContext push() {
    LinkedList<TestContext> list = CONTEXT.get();
    TestContext current = list.getLast();
    TestContext context = new TestContext();
    context.inheritFrom(current);
    list.addLast(context);
    return context;
  }


  static {
    RandomGeneratorFactory<?> factory;
    try {
      // The Javadoc for the java.util.random package says this algorithm is good when there are no special requirements.
      factory = RandomGeneratorFactory.of("L64X128MixRandom");
    } catch (IllegalArgumentException e) {
      // L64X128MixRandom is not supported, use the one with the most state bits
      // SecureRandom has MAX_VALUE stateBits.
      System.getLogger(TestContext.class.getName())
          .log(Level.WARNING, "L64X128MixRandom not found, using alternative. Repeatable random tests may not be repeatable.");
      factory = RandomGeneratorFactory.all()
          .filter(rgf -> !rgf.name().equals("SecureRandom")) // SecureRandom has MAX_VALUE stateBits.
          .filter(RandomGeneratorFactory::isSplittable)
          .max(Comparator.comparingInt(RandomGeneratorFactory<RandomGenerator>::stateBits))
          .orElseThrow();
    }
    randomFactory = factory;
    root = (SplittableGenerator) randomFactory.create();

    DEFAULT = new TestContext();

    CONTEXT = ThreadLocal.withInitial(
        () -> {
          LinkedList<TestContext> list = new LinkedList<>();
          TestContext context = new TestContext();
          context.inheritFrom(DEFAULT);
          list.add(context);
          return list;
        }
    );

    LinkedList<TestContext> list = new LinkedList<>();
    list.add(DEFAULT);
    CONTEXT.set(list);
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

  @Getter
  @Setter
  private int runs = 5;

  @Getter
  private String specSuffix = "$SpecFilter";

  /** The recursion depth of the current test. */
  @Getter
  @Setter
  private int structureDepth = 3;

  private long structureId = 0;


  /** New instance. */
  private TestContext() {
    random = newRandom();
    factories = new FactoryRepository();
  }


  /**
   * Convenience method to add a factory to the repository.
   *
   * @param type  the bean's class
   * @param specs additional specifiers for the bean
   */
  public TestContext addDescription(Class<?> type, Spec... specs) {
    getFactories().addFactory(BeanDescription.create(type, specs));
    return this;
  }


  /**
   * Convenience method to add a factory to the repository.
   *
   * @param description a bean description from which a factory is created
   */
  public TestContext addDescription(BeanDescription description) {
    getFactories().addFactory(description);
    return this;
  }


  /**
   * Add a lookup for bean descriptions.
   *
   * @param lookup the lookup to add
   */
  public TestContext addDescriptionLookup(BeanDescriptionLookup lookup) {
    lookups.add(lookup);
    return this;
  }


  /**
   * Convenience method to add a factory to the repository.
   *
   * @param valueFactory the factory
   */
  public TestContext addFactory(ValueFactory valueFactory) {
    getFactories().addFactory(valueFactory);
    return this;
  }


  /**
   * Add a factory for a specific property.
   *
   * @param clazz        the class that has the property
   * @param propertyName the property's name
   * @param valueFactory the factory to use for just this property
   */
  public TestContext addFactory(Class<?> clazz, String propertyName, ValueFactory valueFactory) {
    getFactories().addFactory(clazz, propertyName, valueFactory);
    return this;
  }


  void beginStructure() {
    structureId++;
  }


  /**
   * Create a bean for the specified class.
   *
   * @param beanClass the bean's class
   *
   * @return the bean instance
   */
  public <T> T create(Class<T> beanClass) {
    return create(beanClass, ValueType.RANDOM);
  }


  /**
   * Create an instance the specified class.
   *
   * @param beanClass the bean's class
   *
   * @return the bean instance
   */
  public <T> T create(Class<T> beanClass, ValueType valueType) {
    return beanClass.cast(getFactories().getFactory(beanClass).create(valueType));
  }


  /**
   * Find an explicit factory for the specified type description.
   *
   * @param clazz the class to find a factory for
   * @param specs additional specifiers for the bean
   *
   * @return the factory if found
   */
  public Optional<BeanDescription> findBeanDescriptionLookup(Class<?> clazz, Spec... specs) {
    for (BeanDescriptionLookup lookup : lookups) {
      Optional<BeanDescription> description = lookup.getDescription(clazz, specs);
      if (description.isPresent()) {
        return description;
      }
    }
    return Optional.empty();
  }


  long getStructureId() {
    return structureId;
  }


  private void inheritFrom(TestContext source) {
    if (source.randomSeed != MAGIC_SEED_NOT_SET) {
      setRepeatable(source.randomSeed);
    }

    Clock delegateClock = source.clock.getDelegate();
    if (delegateClock != null) {
      // We call "withZone" like this to create a new instance of the clock.
      clock.setDelegate(delegateClock.withZone(delegateClock.getZone()));
    }

    specSuffix = source.specSuffix;

    preferWriters = source.preferWriters;

    factories.copy(source.factories);

    lookups.addAll(source.lookups);
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


  /**
   * Set the specifier suffix for finding bean specification filters. This is used to find the class that holds the filter by appending this suffix to the class
   * name. This feature may be disabled by specifying null or an empty string. The suffix can only contain valid Java identifier characters.
   *
   * @param newSpecSuffix the new suffix
   *
   * @return this
   */
  public TestContext setSpecSuffix(String newSpecSuffix) {
    if (newSpecSuffix != null) {
      for (char ch : newSpecSuffix.toCharArray()) {
        if (!Character.isJavaIdentifierPart(ch)) {
          throw new IllegalArgumentException("Spec suffix must be a valid Java identifier");
        }
      }
    } else {
      newSpecSuffix = "";
    }
    specSuffix = newSpecSuffix;
    return this;
  }

}
