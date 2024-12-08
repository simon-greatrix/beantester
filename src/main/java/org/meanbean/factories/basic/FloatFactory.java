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

import org.meanbean.util.RandomValueGenerator;

/**
 * Concrete Factory that creates random Float objects.
 *
 * @author Graham Williamson
 */
public final class FloatFactory extends RandomFactoryBase<Float> {

  /**
   * Construct a new Float object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public FloatFactory(RandomValueGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Float object.
   *
   * @return A new Float object.
   */
  @Override
  public Float create() {
    // Basis of our random number. This value is always positive, so we need to decide the sign
    float result = getRandomValueGenerator().nextFloat();
    // Our float is either based on MAX_VALUE, else MIN_VALUE
    boolean basedOnMax = getRandomValueGenerator().nextBoolean();
    result *= basedOnMax ? Float.MAX_VALUE : Float.MIN_VALUE;
    // Our float is either positive, else negative
    boolean positive = getRandomValueGenerator().nextBoolean();
    result *= positive ? 1 : -1;
    return result;
  }

}
