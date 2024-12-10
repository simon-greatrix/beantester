package io.setl.beantester.factories.basic;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Date objects.
 *
 * @author Graham Williamson
 */
public final class DateFactory extends RandomFactoryBase<Date> {

  private static final long MAX_MILLIS;

  private static final long MIN_MILLIS;

  static {
    long now = System.currentTimeMillis();
    // Start about 3 years in the past
    MIN_MILLIS = now - TimeUnit.DAYS.toMillis(1000);
    // End about 10 years in the future
    MAX_MILLIS = MIN_MILLIS + 0x8000000000L;
  }

  /**
   * Construct a new Date object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public DateFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Date object.
   *
   * @return A new Date object.
   */
  @Override
  public Date create() {
    return new Date(millisSinceEpoch());
  }


  public long millisSinceEpoch() {
    return getRandom().nextLong(MIN_MILLIS, MAX_MILLIS);
  }

}
