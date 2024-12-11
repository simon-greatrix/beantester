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


  /**
   * Create a new Integer object.
   *
   * @return A new Integer object.
   */
  @Override
  public Integer create() {
    return getRandom().nextInt();
  }

}
