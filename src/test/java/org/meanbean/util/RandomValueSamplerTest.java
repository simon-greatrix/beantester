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

package org.meanbean.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.util.Sets;
import org.junit.Test;

public class RandomValueSamplerTest {

  private final int count = 10;

  private final List<Integer> list = IntStream.range(0, 100)
      .mapToObj(Integer::valueOf)
      .collect(Collectors.toList());

  private final Collection<Integer> collection = Sets.newHashSet(list);

  private final RandomValueSampler sampler = new RandomValueSampler(new SimpleRandomValueGenerator());


  private void doTestFindFromCollection(Collection<Integer> input) {
    List<Integer> values = nUniqueCopies(() -> sampler.findFrom(input).get());
    assertThat(values)
        .allMatch(val -> 0 <= val && val < 100)
        .hasSizeGreaterThan(5);

    assertThat(sampler.findFrom(emptySet()))
        .isEmpty();
  }


  private <T> List<T> nUniqueCopies(Supplier<T> supplier) {
    return IntStream.range(0, count)
        .mapToObj(num -> supplier.get())
        .distinct()
        .collect(Collectors.toList());
  }


  @Test
  public void testFindFromCollection() {
    doTestFindFromCollection(collection);
    doTestFindFromCollection(list);
  }


  @Test
  public void testFindFromList() {
    List<Integer> values = nUniqueCopies(() -> sampler.findFrom(list).get());
    assertThat(values)
        .allMatch(val -> 0 <= val && val < 100)
        .hasSizeGreaterThan(5);

    assertThat(sampler.findFrom(emptyList()))
        .isEmpty();
  }


  @Test(expected = IllegalStateException.class)
  public void testGetFromCollectionFail() {
    sampler.getFrom(emptySet());
  }


  @Test
  public void testGetFromCollectionOk() {
    List<Integer> values = nUniqueCopies(() -> sampler.getFrom(collection));
    assertThat(values)
        .allMatch(val -> 0 <= val && val < 100)
        .hasSizeGreaterThan(5);
  }


  @Test(expected = IllegalStateException.class)
  public void testGetFromListFail() {
    sampler.getFrom(emptyList());
  }


  @Test
  public void testGetFromListOk() {
    List<Integer> values = nUniqueCopies(() -> sampler.getFrom(list));
    assertThat(values)
        .allMatch(val -> 0 <= val && val < 100)
        .hasSizeGreaterThan(5);
  }

}
