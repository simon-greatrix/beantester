package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Long objects.
 *
 * @author Graham Williamson
 */
public final class LongValueFactory extends RandomValueFactoryBase<Long> {

  /**
   * Construct a new Long object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public LongValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected Long createPrimary() {
    return 1L;
  }


  /**
   * Create a new Long object.
   *
   * @return A new Long object.
   */
  @Override
  protected Long createRandom() {
    return getRandom().nextLong();
  }


  @Override
  protected Long createSecondary() {
    return 2L;
  }

}
