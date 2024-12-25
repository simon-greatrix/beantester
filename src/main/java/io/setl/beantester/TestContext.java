package io.setl.beantester;

import java.util.Comparator;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

import io.setl.beantester.factories.ValueFactoryRepository;
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

  private RandomClock clock;

  private ValueFactoryRepository valueFactoryRepository;

  private boolean preferWriters = true;

  private RandomGenerator random;


  /** New instance. */
  private TestContext() {
    random = newRandom();

    clock = new RandomClock();
    valueFactoryRepository = new ValueFactoryRepository();
    valueFactoryRepository.loadDefaults();
  }


  /**
   * Convenience method to add a factory to the repository.
   *
   * @param description a bean description from which a factory is created
   */
  public void addFactory(BeanDescription description) {
    valueFactoryRepository.addFactory(description);
  }


  /**
   * Convenience method to add a factory to the repository.
   *
   * @param clazz        the class the factory creates
   * @param valueFactory the factory
   */
  public void addFactory(Class<?> clazz, ValueFactory valueFactory) {
    valueFactoryRepository.addFactory(clazz, valueFactory);
  }


  /**
   * Add a factory for a specific property.
   *
   * @param clazz        the class that has the property
   * @param propertyName the property's name
   * @param valueFactory the factory to use for just this property
   */
  public void addFactory(Class<?> clazz, String propertyName, ValueFactory valueFactory) {
    valueFactoryRepository.addFactory(clazz, propertyName, valueFactory);
  }


  public RandomClock clock() {
    return clock;
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


  public ValueFactoryRepository getFactories() {
    return valueFactoryRepository;
  }


  /**
   * Get the random number generator.
   *
   * @return the random number generator
   */
  public RandomGenerator getRandom() {
    return random;
  }


  /**
   * If a property can be set on creation and via a writer, do we prefer to create a new bean or update the existing one?.
   *
   * @return true if we prefer updating the existing bean, false if we prefer creating a new bean.
   */
  public boolean preferWriters() {
    return preferWriters;
  }


  /**
   * Change the random number generator to be a repeatable one. This allows for repeatable tests.
   *
   * @param seed the seed for the random number generator
   *
   * @return this
   */
  public TestContext repeatable(long seed) {
    random = randomFactory.create(seed);
    return this;
  }


  public TestContext setPreferWriters(boolean preferWriters) {
    this.preferWriters = preferWriters;
    return this;
  }

}
