package io.setl.beantester.factories.basic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random BigDecimal objects.
 */
public class BigDecimalValueFactory extends ValueFactory {

  private static final BigDecimal PRIMARY = new BigDecimal("0.1");

  private static final BigDecimal SECONDARY = new BigDecimal("1.1");


  /**
   * Create a new Double object.
   *
   * @return A new Double object.
   */
  protected static BigDecimal createRandom() {
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
