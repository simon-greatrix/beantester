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


  @Override
  protected String createPrimary() {
    return "x";
  }


  /**
   * Create a new String object.
   *
   * @return A new String object.
   */
  @Override
  protected String createRandom() {
    return Long.toString(getRandom().nextLong() & Long.MAX_VALUE, Character.MAX_RADIX);
  }


  @Override
  protected String createSecondary() {
    return "y";
  }

}
