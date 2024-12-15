package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Integer objects.
 *
 * @author Graham Williamson
 */
public final class IntegerValueFactory extends RandomValueFactoryBase<Integer> {

  /**
   * Construct a new Integer object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public IntegerValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected Integer createPrimary() {
    return 1;
  }


  /**
   * Create a new Integer object.
   *
   * @return A new Integer object.
   */
  @Override
  protected Integer createRandom() {
    return getRandom().nextInt();
  }


  @Override
  protected Integer createSecondary() {
    return 2;
  }

}
