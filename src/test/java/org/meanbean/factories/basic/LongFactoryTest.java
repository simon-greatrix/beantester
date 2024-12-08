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

package org.meanbean.factories.basic;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.meanbean.lang.Factory;
import org.meanbean.util.RandomValueGenerator;

public class LongFactoryTest extends BasicFactoryTestBase<Long> {

  private static final long RANDOM_LONG_1 = 372036854775807L;

  private static final long RANDOM_LONG_2 = -12320685475807L;


  @Override
  protected Factory<Long> createFactory(RandomValueGenerator randomValueGenerator) {
    return new LongFactory(randomValueGenerator);
  }


  @Override
  protected RandomValueGenerator createRandomNumberGenerator() {
    return new ArrayBasedRandomValueGenerator(null, null, new long[]{RANDOM_LONG_1, RANDOM_LONG_2}, null, null,
        null
    );
  }


  @Test
  public void createShouldReturnExpectedLongs() throws Exception {
    Factory<Long> factory = createFactory(createRandomNumberGenerator());
    assertThat("Incorrect random Long.", factory.create(), is(RANDOM_LONG_1));
    assertThat("Incorrect random Long.", factory.create(), is(RANDOM_LONG_2));
  }

}
