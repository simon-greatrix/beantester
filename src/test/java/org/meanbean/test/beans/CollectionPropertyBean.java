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

package org.meanbean.test.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CollectionPropertyBean {

  private List<Date> dates;

  private List<Set<Double>> doubles;

  private Set<Long> longs;

  private Map<Integer, UUID> map;


  public List<Date> getDates() {
    return dates;
  }


  public List<Set<Double>> getDoubles() {
    return doubles;
  }


  public Set<Long> getLongs() {
    return longs;
  }


  public Map<Integer, UUID> getMap() {
    return map;
  }


  public void setDates(List<Date> dates) {
    assertThat(dates).hasOnlyElementsOfType(Date.class);
    this.dates = dates;
  }


  public void setDoubles(List<Set<Double>> doubles) {
    assertThat(doubles).hasOnlyElementsOfType(Set.class);
    assertThat(doubles).allSatisfy(set -> assertThat(set).hasOnlyElementsOfType(Double.class));
    this.doubles = doubles;
  }


  public void setLongs(Set<Long> longs) {
    assertThat(longs).hasOnlyElementsOfType(Long.class);
    this.longs = longs;
  }


  public void setMap(Map<Integer, UUID> map) {
    assertThat(map.keySet()).hasOnlyElementsOfType(Integer.class);
    assertThat(map.values()).hasOnlyElementsOfType(UUID.class);
    this.map = map;
  }

}
