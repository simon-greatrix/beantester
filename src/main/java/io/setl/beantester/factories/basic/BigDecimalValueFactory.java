package io.setl.beantester.factories.basic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random BigDecimal objects.
 *
 * @author Graham Williamson
 */
public final class BigDecimalValueFactory extends RandomValueFactoryBase<BigDecimal> {

  private static final BigDecimal PRIMARY = new BigDecimal("0.1");

  private static final BigDecimal SECONDARY = new BigDecimal("1.1");


  /**
   * Construct a new BigDecimal object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public BigDecimalValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected BigDecimal createPrimary() {
    return PRIMARY;
  }


  /**
   * Create a new Double object.
   *
   * @return A new Double object.
   */
  @Override
  protected BigDecimal createRandom() {
    byte[] bytes = new byte[12];
    getRandom().nextBytes(bytes);
    BigInteger unscaledValue = new BigInteger(bytes);
    int scale = getRandom().nextInt(16) - 8;
    return new BigDecimal(unscaledValue, scale);
  }


  @Override
  protected BigDecimal createSecondary() {
    return SECONDARY;
  }

}
