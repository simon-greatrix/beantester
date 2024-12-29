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
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.ValueType;

/** Load the "java.time.*" factories. */
public class TimeFactories {

  private static final Clock PRIMARY_CLOCK;

  private static final Clock SECONDARY_CLOCK;


  /**
   * Load the "java.time.*" factories.
   *
   * @param repository the repository to load the factories into
   */
  public static void load(FactoryRepository repository) {
    new TimeFactories(repository).doLoad();
  }


  static {
    PRIMARY_CLOCK = Clock.fixed(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    ZoneId zoneId = ZoneId.of("America/New_York");
    SECONDARY_CLOCK = Clock.fixed(ZonedDateTime.of(2021, 7, 15, 12, 30, 30, 0, zoneId).toInstant(), zoneId);
  }


  private final FactoryRepository repository;


  private TimeFactories(FactoryRepository repository) {
    this.repository = repository;
  }


  private <T> void addFactory(Class<T> clazz, Function<ValueType, Object> valueFactory) throws IllegalArgumentException {
    repository.addFactory(new ValueFactory(clazz, valueFactory));
  }


  private Clock clock(ValueType t) {
    if (t == ValueType.PRIMARY) {
      return PRIMARY_CLOCK;
    }
    if (t == ValueType.SECONDARY) {
      return SECONDARY_CLOCK;
    }
    return TestContext.get().getClock();
  }


  private void doLoad() {
    addFactory(java.util.Date.class, (t) -> new java.util.Date(clock(t).millis()));
    addFactory(java.sql.Date.class, (t) -> new java.sql.Date(clock(t).millis()));
    addFactory(java.sql.Timestamp.class, (t) -> new java.sql.Timestamp(clock(t).millis()));

    addFactory(Clock.class, this::clock);
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

    repository.addFactory(newZoneOffsetFactory());
    repository.addFactory(newPeriodFactory());

    addFactory(ZoneId.class, (t) -> RandomClock.randomZoneId());

    addFactory(Duration.class, (t) -> switch (t) {
      case PRIMARY -> Duration.ofMinutes(1);
      case SECONDARY -> Duration.ofHours(5);
      default -> Duration.ofMillis(TestContext.get().getRandom().nextLong(0x1_0000_0000L) - 0x8000_0000L);
    });
  }


  private Function<ValueType, Object> newFactory(Function<Clock, ?> function) {
    return (t) -> function.apply(clock(t));
  }


  private ValueFactory newPeriodFactory() {
    Supplier<Object> random = () -> {
      RandomGenerator randomGenerator = TestContext.get().getRandom();
      int sign = randomGenerator.nextBoolean() ? 1 : -1;
      int years = sign * randomGenerator.nextInt(8);
      int months = sign * randomGenerator.nextInt(12);
      int days = sign * randomGenerator.nextInt(32);
      return Period.of(years, months, days).normalized();
    };

    return new ValueFactory(Period.class, () -> Period.ofDays(1), () -> Period.ofMonths(1), random);
  }


  private ValueFactory newZoneOffsetFactory() {
    ZoneOffset secondary = ZoneOffset.ofHoursMinutes(5, 30);
    Supplier<Object> createRandomZoneOffset = () -> {
      RandomGenerator random = TestContext.get().getRandom();
      int sign = random.nextBoolean() ? 1 : -1;
      int hours = random.nextInt(18);
      int minutes = random.nextInt(59);
      int seconds = random.nextInt(59);
      return ZoneOffset.ofHoursMinutesSeconds(sign * hours, sign * minutes, sign * seconds);
    };

    return new ValueFactory(
        ZoneOffset.class,
        () -> ZoneOffset.UTC,
        () -> secondary,
        createRandomZoneOffset
    );
  }

}
