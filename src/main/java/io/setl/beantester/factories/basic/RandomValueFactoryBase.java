package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

import io.setl.beantester.factories.ValueFactory;
import io.setl.beantester.factories.ValueType;

/**
 * Abstract base class for a Factory that creates random objects of the specified type.
 *
 * @param <T> The data type of the object this Factory creates.
 *
 * @author Graham Williamson
 */
public abstract class RandomValueFactoryBase<T> implements ValueFactory<T> {

  /** Random number generator used by the factory to generate random values. */
  private final RandomGenerator random;


  /**
   * Construct a new Factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public RandomValueFactoryBase(RandomGenerator random) throws IllegalArgumentException {
    this.random = random;
  }


  /**
   * Create a new object of the specified type.
   *
   * @return A new object of the specified type.
   */
  @Override
  public T create(ValueType type) {
    return switch (type) {
      case PRIMARY -> createPrimary();
      case SECONDARY -> createSecondary();
      default -> createRandom();
    };
  }


  protected abstract T createPrimary();


  protected abstract T createRandom();


  protected abstract T createSecondary();


  /**
   * Get the random value generator.
   *
   * @return A random value generator.
   */
  public final RandomGenerator getRandom() {
    return random;
  }

}
