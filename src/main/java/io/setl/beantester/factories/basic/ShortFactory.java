package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Short objects.
 *
 * @author Graham Williamson
 */
public final class ShortFactory extends RandomFactoryBase<Short> {

  /**
   * Construct a new Short object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public ShortFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Short object.
   *
   * @return A new Short object.
   */
  @Override
  public Short create() {
    return (short) getRandom().nextInt(Short.MIN_VALUE, 1 + Short.MAX_VALUE);
  }

}
