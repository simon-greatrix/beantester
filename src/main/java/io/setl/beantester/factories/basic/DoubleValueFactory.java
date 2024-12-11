package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Double objects.
 *
 * @author Graham Williamson
 */
public final class DoubleValueFactory extends RandomValueFactoryBase<Double> {

  /**
   * Construct a new Double object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public DoubleValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
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
