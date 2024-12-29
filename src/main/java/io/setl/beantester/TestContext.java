package io.setl.beantester;

import java.util.Comparator;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

import lombok.Getter;
import lombok.Setter;

import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.factories.time.RandomClock;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.Specs;

/**
 * A class that holds the test context. This is to support multi-threading during testing.
 */
public class TestContext {

  private static final RandomGeneratorFactory<?> randomFactory;

  private static final SplittableGenerator root;

  private static final ThreadLocal<TestContext> CONTEXT = ThreadLocal.withInitial(TestContext::new);


  public static void close() {
    CONTEXT.remove();
    ValueFactory.STACK.remove();
  }


  public static TestContext get() {
    return CONTEXT.get();
  }


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
  }

  @Getter
  @Setter
  private RandomClock clock;

  private boolean factoriesAreInitialized = false;

  private final FactoryRepository factories;

  @Getter @Setter
  private boolean preferWriters = true;

  /**
   * -- GETTER --
   *  Get the random number generator.
   *
   * @return the random number generator
   */
  @Getter
  private RandomGenerator random;


  /** New instance. */
  private TestContext() {
    random = newRandom();

    clock = new RandomClock();
    factories = new FactoryRepository();
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


  /**
   * Get all the factories that are available.
   *
   * @return the factory repository
   */
  public FactoryRepository getFactories() {
    if (!factoriesAreInitialized) {
      factoriesAreInitialized = true;
      factories.loadDefaults();
    }
    return factories;
  }


  /**
   * Change the random number generator to be a repeatable one. This allows for repeatable tests.
   *
   * @param seed the seed for the random number generator
   *
   * @return this
   */
  public TestContext setRepeatable(long seed) {
    random = randomFactory.create(seed);
    return this;
  }

}
