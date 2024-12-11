package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random String objects.
 *
 * @author Graham Williamson
 */
public final class StringValueFactory extends RandomValueFactoryBase<String> {

  /**
   * Construct a new String object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public StringValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  /**
   * Create a new String object.
   *
   * @return A new String object.
   */
  @Override
  public String create() {
    return Long.toString(getRandom().nextLong(), Character.MAX_RADIX);
  }

}
