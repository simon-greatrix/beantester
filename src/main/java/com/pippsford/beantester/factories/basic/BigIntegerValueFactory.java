package com.pippsford.beantester.factories.basic;

import java.math.BigInteger;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory for BigInteger instances.
 */
public class BigIntegerValueFactory extends ValueFactory {

  /** Create a random BigInteger value. */
  public static BigInteger createRandom() {
    // We create a 12 byte random number and use that as the unscaled value. This is too big to fit in a long so we will be able to trap narrowing conversions.
    byte[] bytes = new byte[12];
    TestContext.get().getRandom().nextBytes(bytes);
    return new BigInteger(bytes);
  }


  /**
   * Construct a new BigInteger object factory. The primary value is 1 and the secondary value is 2.
   */
  public BigIntegerValueFactory() {
    super(BigInteger.class, () -> BigInteger.ONE, () -> BigInteger.TWO, BigIntegerValueFactory::createRandom);
  }

}
