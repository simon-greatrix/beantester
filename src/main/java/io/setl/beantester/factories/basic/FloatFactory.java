package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Float objects.
 *
 * @author Graham Williamson
 */
public final class FloatFactory extends RandomFactoryBase<Float> {

  /**
   * Construct a new Float object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public FloatFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Float object.
   *
   * @return A new Float object.
   */
  @Override
  public Float create() {
    return (float) getRandom().nextGaussian();
  }

}
