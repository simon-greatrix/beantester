package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;


/**
 * Concrete Factory that creates random Boolean objects.
 *
 * @author Graham Williamson
 */
public final class BooleanValueFactory extends RandomValueFactoryBase<Boolean> {

  /**
   * Construct a new Boolean object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public BooleanValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected Boolean createPrimary() {
    return null;
  }


  /**
   * Create a new Boolean object.
   *
   * @return A new Boolean object.
   */
  @Override
  protected Boolean createRandom() {
    return getRandom().nextBoolean();
  }


  @Override
  protected Boolean createSecondary() {
    return Boolean.FALSE;
  }

}
