package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;


/**
 * Concrete Factory that creates random Boolean objects.
 *
 * @author Graham Williamson
 */
public final class BooleanFactory extends RandomFactoryBase<Boolean> {

  /**
   * Construct a new Boolean object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public BooleanFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Boolean object.
   *
   * @return A new Boolean object.
   */
  @Override
  public Boolean create() {
    return getRandom().nextBoolean();
  }

}
