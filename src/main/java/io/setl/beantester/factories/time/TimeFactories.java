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
import io.setl.beantester.factories.ValueType;

/** Load the "java.time.*" factories. */
public class TimeFactories {

  private static final Clock PRIMARY_CLOCK;

  private static final Clock SECONDARY_CLOCK;


  /**
   * Load the "java.time.*" factories.
   *
   * @param context    the test context
   * @param repository the repository to load the factories into
   */
  public static void load(TestContext context, ValueFactoryRepository repository) {
    new TimeFactories(context, repository).doLoad();
  }


  static {
    PRIMARY_CLOCK = Clock.fixed(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    ZoneId zoneId = ZoneId.of("America/New_York");
    SECONDARY_CLOCK = Clock.fixed(ZonedDateTime.of(2021, 7, 15, 12, 30, 30, 0, zoneId).toInstant(), zoneId);
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


  private Clock clock(ValueType t) {
    if (t == ValueType.PRIMARY) {
      return PRIMARY_CLOCK;
    }
    if (t == ValueType.SECONDARY) {
      return SECONDARY_CLOCK;
    }
    return clock;
  }


  private void doLoad() {
    addFactory(java.util.Date.class, (t) -> new java.util.Date(clock(t).millis()));
    addFactory(java.sql.Date.class, (t) -> new java.sql.Date(clock(t).millis()));
    addFactory(java.sql.Timestamp.class, (t) -> new java.sql.Timestamp(clock(t).millis()));

    addFactory(Clock.class, (t) -> clock(t));
    addFactory(Instant.class, (t) -> clock(t).instant());
    addFactory(LocalDate.class, newFactory(LocalDate::now));
    addFactory(LocalDateTime.class, newFactory(LocalDateTime::now));
    addFactory(LocalTime.class, newFactory(LocalTime::now));
    addFactory(OffsetDateTime.class, newFactory(OffsetDateTime::now));
    addFactory(OffsetTime.class, newFactory(OffsetTime::now));
    addFactory(MonthDay.class, newFactory(MonthDay::now));
    addFactory(Year.class, newFactory(Year::now));
    addFactory(YearMonth.class, newFactory(YearMonth::now));
    addFactory(ZonedDateTime.class, newFactory(ZonedDateTime::now));

    addFactory(ZoneOffset.class, newZoneOffsetFactory());
    addFactory(Period.class, newPeriodFactory());

    final RandomGenerator random = context.getRandom();
    addFactory(ZoneId.class, (t) -> RandomClock.randomZoneId(random));

    addFactory(Duration.class, (t) -> switch (t) {
      case PRIMARY -> Duration.ofMinutes(1);
      case SECONDARY -> Duration.ofHours(5);
      default -> Duration.ofMillis(random.nextLong(0x1_0000_0000L) - 0x8000_0000L);
    });
  }


  private <T> ValueFactory<T> newFactory(Function<Clock, T> function) {
    return (t) -> function.apply(clock(t));
  }


  private ValueFactory<Period> newPeriodFactory() {
    return (t) -> {
      if (t == ValueType.PRIMARY) {
        return Period.ofDays(1);
      }
      if (t == ValueType.SECONDARY) {
        return Period.ofMonths(1);
      }
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
    return (t) -> {
      if (t == ValueType.PRIMARY) {
        return ZoneOffset.UTC;
      }
      if (t == ValueType.SECONDARY) {
        return ZoneOffset.ofHoursMinutes(5, 30);
      }
      RandomGenerator random = context.getRandom();
      int sign = random.nextBoolean() ? 1 : -1;
      int hours = random.nextInt(18);
      int minutes = random.nextInt(59);
      int seconds = random.nextInt(59);
      return ZoneOffset.ofHoursMinutesSeconds(sign * hours, sign * minutes, sign * seconds);
    };
  }

}
