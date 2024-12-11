package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Byte objects.
 *
 * @author Graham Williamson
 */
public final class ByteValueFactory extends RandomValueFactoryBase<Byte> {

  /**
   * Construct a new Byte object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public ByteValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
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
