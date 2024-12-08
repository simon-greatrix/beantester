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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.SimpleFactoryCollection;
import org.meanbean.lang.Factory;
import org.meanbean.util.RandomValueGenerator;
import org.meanbean.util.SimpleRandomValueGenerator;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConcurrentFactoryPluginTest {

  public static final Class<?>[] FACTORY_CLASSES = {
      Clock.class, Duration.class, Instant.class, LocalDate.class,
      LocalDateTime.class, LocalTime.class, MonthDay.class, OffsetDateTime.class, OffsetTime.class, Period.class,
      Year.class, YearMonth.class, ZonedDateTime.class, ZoneId.class, ZoneOffset.class,
  };

  private final TimePlugin plugin = new TimePlugin();

  private final RandomValueGenerator randomValueGenerator = new SimpleRandomValueGenerator();

  private FactoryCollection factoryCollection;


  @Before
  public void before() {
    factoryCollection = new SimpleFactoryCollection();
  }


  @Test
  public void shouldRegisterCollectionFactories() throws Exception {
    for (Class<?> clazz : FACTORY_CLASSES) {
      assertThat("Factory for class [" + clazz + "] should not be registered prior to plugin initialization.",
          factoryCollection.hasFactory(clazz), is(false)
      );
    }
    plugin.initialize(factoryCollection, randomValueGenerator);
    for (Class<?> clazz : FACTORY_CLASSES) {
      assertThat("Plugin did not register Factory for class [" + clazz + "].",
          factoryCollection.hasFactory(clazz), is(true)
      );
    }
  }


  @Test
  public void testDuration() {
    plugin.initialize(factoryCollection, randomValueGenerator);

    Factory<?> factory = factoryCollection.getFactory(Duration.class);
    Duration val1 = (Duration) factory.create();
    Duration val2 = (Duration) factory.create();
    assertThat(val1, is(not(val2)));
  }


  @Test
  public void testLocalDateTime() {
    plugin.initialize(factoryCollection, randomValueGenerator);

    Factory<?> factory = factoryCollection.getFactory(LocalDateTime.class);
    LocalDateTime val1 = (LocalDateTime) factory.create();
    LocalDateTime val2 = (LocalDateTime) factory.create();
    assertThat(val1, is(not(val2)));
  }


  @Test
  public void testPeriod() {
    plugin.initialize(factoryCollection, randomValueGenerator);

    Factory<?> factory = factoryCollection.getFactory(Period.class);
    Period val1 = (Period) factory.create();
    Period val2 = (Period) factory.create();
    assertThat(val1, is(not(val2)));
  }


  @Test
  public void testZoneOffset() {
    plugin.initialize(factoryCollection, randomValueGenerator);

    Factory<?> factory = factoryCollection.getFactory(ZoneOffset.class);
    List<ZoneOffset> zoneOffsets = IntStream.range(0, 10)
        .mapToObj(num -> (ZoneOffset) factory.create())
        .distinct()
        .collect(Collectors.toList());
    assertThat(zoneOffsets.size(), is(greaterThan(7)));
  }

}
