package com.pippsford.beantester.factories.time;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import com.pippsford.beantester.TestContext;

/**
 * An alternative random clock that acts a little bit more like a real clock in that is intended to report increasing times.
 */
public class RandomIncreasingClock extends Clock {

  private long currentTimeMillis;

  /** The clock goes forward this much every time it is asked for the time. */
  private long fixedJump;

  /** The clock goes forward at most this much every time it is asked for the time. */
  private long maxRandomJump;

  /** The clock goes forward at least this much every time it is asked for the time. */
  private long minRandomJump;

  private ZoneId zoneId;


  /**
   * New instance.
   */
  public RandomIncreasingClock() {
    zoneId = RandomClock.randomZoneId();
    currentTimeMillis = RandomClock.generateMillisSinceEpoch();
    fixedJump = 0;
    maxRandomJump = 3_600_000; // 1 hour
    minRandomJump = -10; // could be a clock skew
  }


  /**
   * Create a copy of a clock with a new zone ID.
   *
   * @param original the clock to copy
   * @param zoneId   the new zone ID
   */
  public RandomIncreasingClock(RandomIncreasingClock original, ZoneId zoneId) {
    this.currentTimeMillis = original.currentTimeMillis;
    this.fixedJump = original.fixedJump;
    this.maxRandomJump = original.maxRandomJump;
    this.minRandomJump = original.minRandomJump;
    this.zoneId = zoneId;
  }


  public long currentTimeMillis() {
    return currentTimeMillis;
  }


  public long fixedJump() {
    return fixedJump;
  }


  @Override
  public ZoneId getZone() {
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
    long newTime = currentTimeMillis + fixedJump + TestContext.get().getRandom().nextLong(minRandomJump, maxRandomJump);
    currentTimeMillis = newTime;
    return Instant.ofEpochMilli(newTime);
  }


  /**
   * Maximum (exclusive) number of milliseconds the clock can jump forward in addition to the fixed jump.
   *
   * @return the maximum random jump
   */
  public long maxRandomJump() {
    return maxRandomJump;
  }


  /**
   * Minimum (inclusive) number of milliseconds the clock can jump forward in addition to the fixed jump.
   *
   * @return the minimum random jump
   */
  public long minRandomJump() {
    return minRandomJump;
  }


  /**
   * Set this clock's current time.
   *
   * @param currentTimeMillis the current time
   *
   * @return this clock
   */
  public RandomIncreasingClock setCurrentTimeMillis(long currentTimeMillis) {
    this.currentTimeMillis = currentTimeMillis;
    return this;
  }


  /**
   * Set the fixed jump of this clock. The clock goes forward this much plus a random amount every time it is asked for the time.
   *
   * @param fixedJump the fixed jump
   *
   * @return this clock
   */
  public RandomIncreasingClock setFixedJump(long fixedJump) {
    this.fixedJump = fixedJump;
    return this;
  }


  /**
   * Set the maximum random jump for this clock.
   *
   * @param maxRandomJump the maximum random jump
   *
   * @return this clock
   */
  public RandomIncreasingClock setMaxRandomJump(long maxRandomJump) {
    this.maxRandomJump = maxRandomJump;
    return this;
  }


  /**
   * Set the minimum random jump for this clock.
   *
   * @param minRandomJump the maximum random jump
   *
   * @return this clock
   */
  public RandomIncreasingClock setMinRandomJump(long minRandomJump) {
    this.minRandomJump = minRandomJump;
    return this;
  }


  public RandomIncreasingClock setZoneId(ZoneId zoneId) {
    this.zoneId = zoneId;
    return this;
  }


  @Override
  public Clock withZone(ZoneId zone) {
    return new RandomIncreasingClock(this, zoneId);
  }

}
