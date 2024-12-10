package io.setl.beantester.factories.basic;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import io.setl.beantester.factories.Factory;

/**
 * Abstract base class for a Factory that creates random objects of the specified type.
 *
 * @param <T> The data type of the object this Factory creates.
 *
 * @author Graham Williamson
 */
public abstract class RandomFactoryBase<T> implements Factory<T> {

  public static <T> Optional<T> findFrom(RandomGenerator random, Collection<T> collection) {
    if (collection.isEmpty()) {
      return Optional.empty();
    }
    int index = random.nextInt(collection.size());
    if (collection instanceof List && collection instanceof RandomAccess) {
      return Optional.of(((List<T>) collection).get(index));
    }

    Iterator<T> iterator = collection.iterator();
    for (int i = 0; i < index; i++) {
      iterator.next();
    }
    return Optional.ofNullable(iterator.next());
  }


  public static <T> T getFrom(RandomGenerator random, Collection<T> collection) {
    if (collection.isEmpty()) {
      return null;
    }
    int index = random.nextInt(collection.size());
    if (collection instanceof List && collection instanceof RandomAccess) {
      return ((List<T>) collection).get(index);
    }

    Iterator<T> iterator = collection.iterator();
    for (int i = 0; i < index; i++) {
      iterator.next();
    }
    return iterator.next();
  }


  public static RandomGenerator newRandom() {
    try {
      // The Javadoc for the java.util.random package says this algorithm is good when there are no special requirements.
      return RandomGeneratorFactory.of("L64X128MixRandom").create();
    } catch (IllegalArgumentException e) {
      // L64X128MixRandom is not supported, use the one with the most state bits
      return RandomGeneratorFactory.all()
          .filter(rgf -> !rgf.name().equals("SecureRandom")) // SecureRandom has MAX_VALUE stateBits.
          .sorted(Comparator.comparingInt(RandomGeneratorFactory<RandomGenerator>::stateBits).reversed())
          .findFirst()
          .orElse(RandomGeneratorFactory.of("Random"))
          .create();
    }
  }

  /** Random number generator used by the factory to generate random values. */
  private final RandomGenerator randomValueGenerator;


  /**
   * Construct a new Factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public RandomFactoryBase(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    this.randomValueGenerator = randomValueGenerator;
  }


  /**
   * Create a new object of the specified type.
   *
   * @return A new object of the specified type.
   */
  @Override
  public abstract T create();


  /**
   * Get the random value generator.
   *
   * @return A random value generator.
   */
  public final RandomGenerator getRandom() {
    return randomValueGenerator;
  }

}
