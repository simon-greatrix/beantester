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

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.ValueFactory;
import io.setl.beantester.factories.ValueFactoryRepository;

public class TimeFactories {

  public static void load(TestContext context, ValueFactoryRepository repository) {
    new TimeFactories(context, repository).load();
  }


  private final Clock clock;

  private final TestContext context;

  private final ValueFactoryRepository repository;


  private TimeFactories(TestContext context, ValueFactoryRepository repository) {
    this.context = context;
    this.repository = repository;
    this.clock = context.clock();
  }


  private <T> void addFactory(Class<T> clazz, ValueFactory<T> valueFactory) throws IllegalArgumentException {
    repository.addFactory(clazz, valueFactory);
  }


  private void load() {
    RandomGenerator random = context.getRandom();
    addFactory(java.util.Date.class, () -> new java.util.Date(clock.millis()));
    addFactory(java.sql.Date.class, () -> new java.sql.Date(clock.millis()));
    addFactory(java.sql.Timestamp.class, () -> new java.sql.Timestamp(clock.millis()));

    addFactory(Clock.class, () -> clock);
    addFactory(Instant.class, clock::instant);
    addFactory(LocalDate.class, newFactory(LocalDate::now));
    addFactory(LocalDateTime.class, newFactory(LocalDateTime::now));
    addFactory(LocalTime.class, newFactory(LocalTime::now));
    addFactory(OffsetDateTime.class, newFactory(OffsetDateTime::now));
    addFactory(OffsetTime.class, newFactory(OffsetTime::now));
    addFactory(MonthDay.class, newFactory(MonthDay::now));
    addFactory(Year.class, newFactory(Year::now));
    addFactory(YearMonth.class, newFactory(YearMonth::now));
    addFactory(ZonedDateTime.class, newFactory(ZonedDateTime::now));
    addFactory(ZoneId.class, () -> RandomClock.randomZoneId(random));
    addFactory(ZoneOffset.class, newZoneOffsetFactory());

    addFactory(Duration.class, () -> Duration.ofMillis(random.nextLong(0x1_0000_0000L) - 0x8000_0000L));
    addFactory(Period.class, newPeriodFactory());
  }


  private <T> ValueFactory<T> newFactory(Function<Clock, T> function) {
    return () -> function.apply(clock);
  }


  private ValueFactory<Period> newPeriodFactory() {
    return () -> {
      RandomGenerator random = context.getRandom();
      if (random.nextBoolean()) {
        return Period.ofDays(random.nextInt(4096) - 2048);
      } else {
        int sign = random.nextBoolean() ? 1 : -1;
        int years = sign * random.nextInt(8);
        int months = sign * random.nextInt(12);
        int days = sign * random.nextInt(32);
        return Period.of(years, months, days).normalized();
      }
    };
  }


  private ValueFactory<ZoneOffset> newZoneOffsetFactory() {
    return () -> {
      RandomGenerator random = context.getRandom();
      int sign = random.nextBoolean() ? 1 : -1;
      int hours = random.nextInt(18);
      int minutes = random.nextInt(59);
      int seconds = random.nextInt(59);
      return ZoneOffset.ofHoursMinutesSeconds(sign * hours, sign * minutes, sign * seconds);
    };
  }

}
