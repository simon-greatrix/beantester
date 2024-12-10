package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Byte objects.
 *
 * @author Graham Williamson
 */
public final class ByteFactory extends RandomFactoryBase<Byte> {

  /**
   * Construct a new Byte object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public ByteFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Byte object.
   *
   * @return A new Byte object.
   */
  @Override
  public Byte create() {
    return (byte) getRandom().nextInt(256);
  }

}
