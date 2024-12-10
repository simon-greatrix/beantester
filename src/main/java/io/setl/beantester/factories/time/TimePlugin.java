package io.setl.beantester.factories.time;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.Factory;
import io.setl.beantester.factories.FactoryCollection;
import io.setl.beantester.factories.FactoryCollectionPlugin;
import io.setl.beantester.factories.NoSuchFactoryException;

public class TimePlugin implements FactoryCollectionPlugin {

  private Clock clock;

  private FactoryCollection factoryCollection;

  private RandomGenerator randomValueGenerator;


  public TimePlugin() {
    // empty
  }


  public TimePlugin(Clock clock) {
    this.clock = clock;
  }


  private <T> void addFactory(Class<T> clazz, Factory<T> factory) throws IllegalArgumentException {
    factoryCollection.addFactory(clazz, factory);
  }


  @SuppressWarnings("unchecked")
  private <T> Factory<T> getFactory(Class<T> clazz) throws IllegalArgumentException, NoSuchFactoryException {
    return factoryCollection.getFactory(clazz);
  }


  @Override
  public void initialize(FactoryCollection factoryCollection, RandomGenerator randomValueGenerator) {

    this.randomValueGenerator = randomValueGenerator;
    this.factoryCollection = factoryCollection;
    if (this.clock == null) {
      this.clock = new RandomClock(randomValueGenerator);
    }

    addFactory(Clock.class, () -> clock);
    addFactory(Instant.class, newFactory(Instant::now));
    addFactory(LocalDate.class, newFactory(LocalDate::now));
    addFactory(LocalDateTime.class, newFactory(LocalDateTime::now));
    addFactory(LocalTime.class, newFactory(LocalTime::now));
    addFactory(OffsetDateTime.class, newFactory(OffsetDateTime::now));
    addFactory(OffsetTime.class, newFactory(OffsetTime::now));
    addFactory(MonthDay.class, newFactory(MonthDay::now));
    addFactory(Year.class, newFactory(Year::now));
    addFactory(YearMonth.class, newFactory(YearMonth::now));
    addFactory(ZonedDateTime.class, newFactory(ZonedDateTime::now));
    addFactory(ZoneId.class, clock::getZone);
    addFactory(ZoneOffset.class, newZoneOffsetFactory());

    addFactory(Duration.class, () -> Duration.ofMillis(randomValueGenerator.nextLong(0x1_0000_0000L) - 0x8000_0000L));
    addFactory(Period.class, newPeroidFactory());
  }


  private <T> Factory<T> newFactory(Function<Clock, T> function) {
    return () -> function.apply(clock);
  }


  private Factory<Period> newPeroidFactory() {
    return () -> {
      if (randomValueGenerator.nextBoolean()) {
        return Period.ofDays(randomValueGenerator.nextInt(4096) - 2048);
      } else {
        int sign = randomValueGenerator.nextBoolean() ? 1 : -1;
        int years = sign * randomValueGenerator.nextInt(8);
        int months = sign * randomValueGenerator.nextInt(12);
        int days = sign * randomValueGenerator.nextInt(32);
        return Period.of(years, months, days).normalized();
      }
    };
  }


  private Factory<ZoneOffset> newZoneOffsetFactory() {
    return () -> {
      int sign = randomValueGenerator.nextBoolean() ? 1 : -1;
      int hours = randomValueGenerator.nextInt(18);
      int minutes = randomValueGenerator.nextInt(59);
      int seconds = randomValueGenerator.nextInt(59);
      return ZoneOffset.ofHoursMinutesSeconds(sign * hours, sign * minutes, sign * seconds);
    };
  }

}
