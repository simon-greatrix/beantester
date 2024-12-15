package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Float objects.
 *
 * @author Graham Williamson
 */
public final class FloatValueFactory extends RandomValueFactoryBase<Float> {

  /**
   * Construct a new Float object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public FloatValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected Float createPrimary() {
    return 1f;
  }


  /**
   * Create a new Float object.
   *
   * @return A new Float object.
   */
  @Override
  protected Float createRandom() {
    return (float) getRandom().nextGaussian();
  }


  @Override
  protected Float createSecondary() {
    return 1.25f;
  }

}
