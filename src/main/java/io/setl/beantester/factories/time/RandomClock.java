package io.setl.beantester.factories.time;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.Sampler;

/**
 * This random clock reports a different time in every call to {@link #instant()}.
 *
 * <p>The clock reports time from approximately 3 years in the past to 10 years in the future.</p>
 */
public class RandomClock extends Clock {

  private static final long MAX_MILLIS;

  private static final long MIN_MILLIS;


  static long generateMillisSinceEpoch(RandomGenerator random) {
    return random.nextLong(MIN_MILLIS, MAX_MILLIS);
  }


  /**
   * Select a random ZoneId.
   *
   * @param random the random generator
   *
   * @return a random ZoneId
   */
  public static ZoneId randomZoneId(RandomGenerator random) {
    String zoneId = Sampler.getFrom(random, ZoneId.getAvailableZoneIds());
    try {
      return zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
    } catch (DateTimeException e) {
      return ZoneId.systemDefault();
    }
  }


  static {
    long now = System.currentTimeMillis();
    // Start about 3 years in the past
    MIN_MILLIS = now - TimeUnit.DAYS.toMillis(1000);
    // End about 10 years in the future
    MAX_MILLIS = MIN_MILLIS + 0x8000000000L;
  }

  private final RandomGenerator random;

  private Clock delegate;

  private ZoneId zoneId;


  /**
   * New instance.
   *
   * @param random the random generator
   */
  public RandomClock(RandomGenerator random) {
    this.random = random;
    zoneId = randomZoneId(random);
  }


  /**
   * Get the delegate clock, if any. The delegate clock replaces this clock when it is set.
   *
   * @return the delegate clock, or null if there is none
   */
  public Clock delegate() {
    return delegate;
  }


  @Override
  public ZoneId getZone() {
    Clock d = delegate;
    if (d != null) {
      return d.getZone();
    }
    return zoneId;
  }


  /**
   * Get the current time of this clock.
   *
   * <p>The clock updates every time this method is called, increasing the number of milliseconds by a fixed amount and then an additional random amount. </p>
   *
   * @return the current time
   */
  @Override
  public Instant instant() {
    Clock d = delegate;
    if (d != null) {
      return d.instant();
    }
    return Instant.ofEpochMilli(generateMillisSinceEpoch(random));
  }


  /**
   * Set the delegate clock. The delegate clock replaces this clock when it is set.
   *
   * @param delegate the delegate clock (or null to remove)
   *
   * @return this clock
   */
  public RandomClock setDelegate(Clock delegate) {
    this.delegate = delegate;
    return this;
  }


  public RandomClock setZoneId(ZoneId zoneId) {
    this.zoneId = zoneId;
    return this;
  }


  @Override
  public Clock withZone(ZoneId zone) {
    return new Clock() {
      @Override
      public ZoneId getZone() {
        return zone;
      }


      @Override
      public Instant instant() {
        return RandomClock.this.instant();
      }


      @Override
      public Clock withZone(ZoneId zone) {
        return RandomClock.this.withZone(zone);
      }
    };
  }

}
