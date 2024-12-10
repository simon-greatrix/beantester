package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random String objects.
 *
 * @author Graham Williamson
 */
public final class StringFactory extends RandomFactoryBase<String> {

  /**
   * Construct a new String object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public StringFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
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
