package io.setl.beantester.factories.basic;

import java.math.BigInteger;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Factory for BigInteger instances.
 */
public class BigIntegerValueFactory extends ValueFactory {

  protected static BigInteger createRandom() {
    byte[] bytes = new byte[12];
    TestContext.get().getRandom().nextBytes(bytes);
    return new BigInteger(bytes);
  }


  /**
   * Construct a new BigInteger object factory.
   */
  public BigIntegerValueFactory() {
    super(() -> BigInteger.ONE, () -> BigInteger.TWO, BigIntegerValueFactory::createRandom);
  }

}
