package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Long objects.
 *
 * @author Graham Williamson
 */
public final class LongFactory extends RandomFactoryBase<Long> {

  /**
   * Construct a new Long object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public LongFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Long object.
   *
   * @return A new Long object.
   */
  @Override
  public Long create() {
    return getRandom().nextLong();
  }

}
