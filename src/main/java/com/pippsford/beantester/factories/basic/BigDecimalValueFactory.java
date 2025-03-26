package com.pippsford.beantester.factories.basic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.random.RandomGenerator;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory that creates random BigDecimal objects.
 */
public class BigDecimalValueFactory extends ValueFactory {

  /** The primary value is 0.1. */
  private static final BigDecimal PRIMARY = new BigDecimal("0.1");

  /** The secondary value is 1.1. */
  private static final BigDecimal SECONDARY = new BigDecimal("1.1");


  /**
   * Create a new BigDecimal object.
   *
   * @return A new BigDecimal object.
   */
  protected static BigDecimal createRandom() {
    // We create a 12 byte random number and use that as the unscaled value, and then scale it randomly.
    // 12 bytes ensures that the number is too large to fit in a long or double, so we will be able to spot narrowing conversions.
    byte[] bytes = new byte[12];
    RandomGenerator random = TestContext.get().getRandom();
    random.nextBytes(bytes);
    BigInteger unscaledValue = new BigInteger(bytes);
    int scale = random.nextInt(16) - 8;
    return new BigDecimal(unscaledValue, scale);
  }


  /**
   * Construct a new BigDecimal object factory.
   */
  public BigDecimalValueFactory() {
    super(BigDecimal.class, () -> PRIMARY, () -> SECONDARY, BigDecimalValueFactory::createRandom);
  }

}
