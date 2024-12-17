package io.setl.beantester.factories.basic;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

/**
 * Factory for BigInteger instances.
 */
public final class BigIntegerValueFactory extends RandomValueFactoryBase<BigInteger> {

  public BigIntegerValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected BigInteger createPrimary() {
    return BigInteger.ONE;
  }


  @Override
  protected BigInteger createRandom() {
    byte[] bytes = new byte[12];
    getRandom().nextBytes(bytes);
    return new BigInteger(bytes);
  }


  @Override
  protected BigInteger createSecondary() {
    return BigInteger.TWO;
  }

}
