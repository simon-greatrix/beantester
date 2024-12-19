package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Double objects.
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


  @Override
  protected Double createPrimary() {
    return 1.0;
  }


  /**
   * Create a new Double object.
   *
   * @return A new Double object.
   */
  @Override
  protected Double createRandom() {
    return getRandom().nextGaussian();
  }


  @Override
  protected Double createSecondary() {
    return 1.25;
  }

}
