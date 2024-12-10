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

import static org.meanbean.test.BeanTesterBuilder.newBeanTesterBuilderWithInheritedContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.meanbean.bean.info.BeanInformationFactory;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.util.FactoryLookupStrategy;
import org.meanbean.lang.Factory;
import org.meanbean.mirror.SerializableLambdas.SerializableFunction1;
import org.meanbean.util.RandomValueGenerator;
import org.meanbean.util.ServiceFactory;
import org.meanbean.util.ServiceFactory.ContextId;

class VerifierSettingsImpl implements VerifierSettings {

  private final Map<Class<?>, Configuration> customConfigurations = new ConcurrentHashMap<>();;

  private BeanInformationFactory beanInformationFactory;

  private BeanPropertyTester beanPropertyTester;

  private FactoryCollection factoryCollection;

  private FactoryLookupStrategy factoryLookupStrategy;

  private RandomValueGenerator randomValueGenerator;

  private final ContextId contextId;

  public VerifierSettingsImpl() {
    contextId = ServiceFactory.createContext();
    randomValueGenerator = RandomValueGenerator.getInstance();
    factoryCollection = FactoryCollection.getInstance();
    factoryLookupStrategy = FactoryLookupStrategy.getInstance();
    beanInformationFactory = BeanInformationFactory.getInstance();
    beanPropertyTester = new BeanPropertyTester();
  }


  @Override
  public BeanInformationFactory getBeanInformationFactory() {
    return beanInformationFactory;
  }


  @Override
  public int getDefaultIterations() {
    return builder.getDefaultIterations();
  }


  @Override
  public FactoryCollection getFactoryCollection() {
    return builder.getFactoryCollection();
  }


  @Override
  public FactoryLookupStrategy getFactoryLookupStrategy() {
    return builder.getFactoryLookupStrategy();
  }


  @Override
  public RandomValueGenerator getRandomValueGenerator() {
    return builder.getRandomValueGenerator();
  }

}
