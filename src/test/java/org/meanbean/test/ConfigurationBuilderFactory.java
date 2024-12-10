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

package org.meanbean.test;

import java.util.HashMap;
import java.util.Map;

import org.meanbean.factories.basic.LongFactory;
import org.meanbean.factories.basic.StringFactory;
import org.meanbean.lang.Factory;
import org.meanbean.util.RandomValueGenerator;
import org.meanbean.util.SimpleRandomValueGenerator;

public class ConfigurationBuilderFactory {

  public static final String IGNORE_PROPERTY_1 = "TEST_IGNORE_PROPERTY_1";

  public static final String IGNORE_PROPERTY_2 = "TEST_IGNORE_PROPERTY_2";

  public static final String IGNORE_PROPERTY_3 = "TEST_IGNORE_PROPERTY_3";

  public static final int ITERATIONS = 4637;

  public static final String OVERRIDE_PROPERTY_1 = "TEST_IGNORE_PROPERTY_1";

  public static final String OVERRIDE_PROPERTY_2 = "TEST_IGNORE_PROPERTY_2";

  private static final RandomValueGenerator RANDOM_NUMBER_GENERATOR = new SimpleRandomValueGenerator();

  public static final LongFactory OVERRIDE_FACTORY_1 = new LongFactory(RANDOM_NUMBER_GENERATOR);

  public static final StringFactory OVERRIDE_FACTORY_2 = new StringFactory(RANDOM_NUMBER_GENERATOR);


  public static ConfigurationBuilder create() {
    String[] ignoreProperties = {IGNORE_PROPERTY_1, IGNORE_PROPERTY_2, IGNORE_PROPERTY_3};
    Map<String, Factory<?>> overrideFactories = new HashMap<String, Factory<?>>();
    overrideFactories.put(OVERRIDE_PROPERTY_1, OVERRIDE_FACTORY_1);
    overrideFactories.put(OVERRIDE_PROPERTY_2, OVERRIDE_FACTORY_2);
    return create(ITERATIONS, ignoreProperties, overrideFactories);
  }


  public static ConfigurationBuilder create(
      int iterations, String[] ignoreProperties,
      Map<String, Factory<?>> overrideFactories
  ) {
    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.iterations(iterations);
    for (String property : ignoreProperties) {
      configurationBuilder.ignore(property);
    }
    for (Map.Entry<String, Factory<?>> override : overrideFactories.entrySet()) {
      configurationBuilder.factory(override.getKey(), override.getValue());
    }
    return configurationBuilder;
  }

}
