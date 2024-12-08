/*-
 * ​​​
 * meanbean
 * ⁣⁣⁣
 * Copyright (C) 2010 - 2020 the original author or authors.
 * ⁣⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ﻿﻿﻿﻿﻿
 */

package org.meanbean.factories.time;

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

import org.kohsuke.MetaInfServices;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.FactoryCollectionPlugin;
import org.meanbean.factories.NoSuchFactoryException;
import org.meanbean.lang.Factory;
import org.meanbean.util.RandomValueGenerator;

@MetaInfServices
public class TimePlugin implements FactoryCollectionPlugin {

  private Clock clock;

  private FactoryCollection factoryCollection;

  private RandomValueGenerator randomValueGenerator;


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
  public void initialize(FactoryCollection factoryCollection, RandomValueGenerator randomValueGenerator) {

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

    addFactory(Duration.class, newDurationFactory());
    addFactory(Period.class, newPeroidFactory());
  }


  private Factory<Duration> newDurationFactory() {
    Factory<LocalDateTime> localDateTimeFactory = getFactory(LocalDateTime.class);
    return () -> Duration.between(localDateTimeFactory.create(), localDateTimeFactory.create());
  }


  private <T> Factory<T> newFactory(Function<Clock, T> function) {
    return () -> function.apply(clock);
  }


  private Factory<Period> newPeroidFactory() {
    Factory<LocalDate> localDateFactory = getFactory(LocalDate.class);
    return () -> Period.between(localDateFactory.create(), localDateFactory.create());
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
