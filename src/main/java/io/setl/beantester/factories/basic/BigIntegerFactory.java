package io.setl.beantester.factories.basic;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

public final class BigIntegerFactory extends RandomFactoryBase<BigInteger> {

  public BigIntegerFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  @Override
  public BigInteger create() {
    byte[] bytes = new byte[12];
    getRandom().nextBytes(bytes);
    return new BigInteger(bytes);
  }

}
