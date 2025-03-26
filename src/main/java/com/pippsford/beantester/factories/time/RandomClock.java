package com.pippsford.beantester.factories.time;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.factories.Sampler;

/**
 * This random clock reports a different time in every call to {@link #instant()}.
 *
 * <p>The clock reports time from approximately 3 years in the past to 10 years in the future.</p>
 */
public class RandomClock extends Clock {

  private static final long MAX_MILLIS;

  private static final long MIN_MILLIS;


  static long generateMillisSinceEpoch() {
    return TestContext.get().getRandom().nextLong(MIN_MILLIS, MAX_MILLIS);
  }


  /**
   * Select a random ZoneId.
   *
   * @return a random ZoneId
   */
  public static ZoneId randomZoneId() {
    String zoneId = Sampler.getFrom(ZoneId.getAvailableZoneIds());
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

  /** A delegate clock that replaces this clock when it is set. */
  @Getter
  @Setter
  private Clock delegate;

  private ZoneId zoneId;


  public RandomClock() {
    // do nothing
  }


  /**
   * Create a copy of a clock with a new zone ID.
   *
   * @param original the original clock
   * @param zoneId   the new zone ID
   */
  public RandomClock(RandomClock original, ZoneId zoneId) {
    if (original.delegate == null) {
      this.delegate = null;
    } else {
      this.delegate = original.delegate.withZone(getZone());
    }
    this.zoneId = zoneId;
  }


  @Override
  public ZoneId getZone() {
    Clock d = delegate;
    if (d != null) {
      return d.getZone();
    }

    if (zoneId == null) {
      zoneId = randomZoneId();
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
    return Instant.ofEpochMilli(generateMillisSinceEpoch());
  }


  /**
   * Set the zone ID used by this clock.
   *
   * @param zoneId the zone ID
   *
   * @return this
   */
  public RandomClock setZoneId(ZoneId zoneId) {
    this.zoneId = zoneId;
    return this;
  }


  @Override
  public Clock withZone(ZoneId zone) {
    return new RandomClock(this, zone);
  }

}
