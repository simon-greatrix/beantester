package io.setl.beantester;

import java.util.Comparator;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

import io.setl.beantester.factories.ValueFactoryRepository;
import io.setl.beantester.factories.time.RandomClock;

/**
 * A class that holds the test context. This is to support multi-threading during testing.
 */
public class TestContext {


  private static final SplittableGenerator root;


  public static RandomGenerator newRandom() {
    return root.split();
  }


  static {
    SplittableGenerator generator;
    try {
      // The Javadoc for the java.util.random package says this algorithm is good when there are no special requirements.
      generator = SplittableGenerator.of("L64X128MixRandom");
    } catch (IllegalArgumentException e) {
      // L64X128MixRandom is not supported, use the one with the most state bits
      // SecureRandom has MAX_VALUE stateBits.
      generator = (SplittableGenerator) RandomGeneratorFactory.all()
          .filter(rgf -> !rgf.name().equals("SecureRandom")) // SecureRandom has MAX_VALUE stateBits.
          .filter(RandomGeneratorFactory::isSplittable).max(Comparator.comparingInt(RandomGeneratorFactory<RandomGenerator>::stateBits))
          .orElseThrow()
          .create();
    }
    root = generator;
  }

  private final RandomClock clock;

  private final RandomGenerator random;

  private final ValueFactoryRepository valueFactoryRepository;

  private boolean preferWriters = true;


  /** New instance. */
  public TestContext() {
    random = newRandom();
    clock = new RandomClock(random);
    valueFactoryRepository = new ValueFactoryRepository();
    valueFactoryRepository.loadDefaults(this);
  }


  public RandomClock clock() {
    return clock;
  }


  public ValueFactoryRepository getFactories() {
    return valueFactoryRepository;
  }


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


  public TestContext setPreferWriters(boolean preferWriters) {
    this.preferWriters = preferWriters;
    return this;
  }

}
