package io.setl.beantester;

import java.time.Clock;
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
      generator = (SplittableGenerator) RandomGeneratorFactory.all()
          .filter(rgf -> !rgf.name().equals("SecureRandom")) // SecureRandom has MAX_VALUE stateBits.
          .filter(RandomGeneratorFactory::isSplittable)
          .sorted(Comparator.comparingInt(RandomGeneratorFactory<RandomGenerator>::stateBits).reversed())
          .findFirst()
          .orElseThrow()
          .create();
    }
    root = generator;
  }

  private RandomGenerator random;

  private ValueFactoryRepository valueFactoryRepository;

  private final RandomClock clock;

  public TestContext() {
    random = newRandom();
    clock = new RandomClock(random);
    valueFactoryRepository = new ValueFactoryRepository();
    valueFactoryRepository.loadDefaults(this);
  }


  public RandomClock clock() {
    return clock;
  }


  public RandomGenerator getRandom() {
    return random;
  }


  public ValueFactoryRepository getValueFactoryRepository() {
    return valueFactoryRepository;
  }

}
