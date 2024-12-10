package io.setl.beantester.factories.time;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.basic.DateFactory;
import io.setl.beantester.factories.basic.RandomFactoryBase;

public class RandomClock extends Clock {

  private final DateFactory dateFactory;

  private final RandomGenerator randomValueGenerator;


  public RandomClock(RandomGenerator randomValueGenerator) {
    this.randomValueGenerator = randomValueGenerator;
    dateFactory = new DateFactory(randomValueGenerator);
  }


  @Override
  public ZoneId getZone() {
    String zoneId = RandomFactoryBase.getFrom(randomValueGenerator, ZoneId.getAvailableZoneIds());
    return zoneId != null ? ZoneId.of(zoneId) : ZoneId.systemDefault();
  }


  @Override
  public Instant instant() {
    return Instant.ofEpochMilli(dateFactory.millisSinceEpoch());
  }


  @Override
  public Clock withZone(ZoneId zone) {
    throw new UnsupportedOperationException();
  }

}
