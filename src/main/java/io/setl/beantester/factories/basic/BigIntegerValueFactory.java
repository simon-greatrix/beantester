package io.setl.beantester.factories.basic;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

public final class BigIntegerValueFactory extends RandomValueFactoryBase<BigInteger> {

  public BigIntegerValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  public BigInteger create() {
    byte[] bytes = new byte[12];
    getRandom().nextBytes(bytes);
    return new BigInteger(bytes);
  }

}
