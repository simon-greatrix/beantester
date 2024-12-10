package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Double objects.
 *
 * @author Graham Williamson
 */
public final class DoubleFactory extends RandomFactoryBase<Double> {

  /**
   * Construct a new Double object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public DoubleFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Double object.
   *
   * @return A new Double object.
   */
  @Override
  public Double create() {
    return getRandom().nextGaussian();
  }

}
