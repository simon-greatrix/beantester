package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Short objects.
 *
 * @author Graham Williamson
 */
public final class ShortValueFactory extends RandomValueFactoryBase<Short> {

  /**
   * Construct a new Short object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public ShortValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected Short createPrimary() {
    return (short) 1;
  }


  /**
   * Create a new Short object.
   *
   * @return A new Short object.
   */
  @Override
  protected Short createRandom() {
    return (short) getRandom().nextInt(Short.MIN_VALUE, 1 + Short.MAX_VALUE);
  }


  @Override
  protected Short createSecondary() {
    return (short) 2;
  }

}
