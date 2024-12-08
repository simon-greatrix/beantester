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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meanbean.bean.info.BeanInformation;
import org.meanbean.bean.info.BeanInformationFactory;
import org.meanbean.bean.info.JavaBeanInformationFactory;
import org.meanbean.bean.info.PropertyInformationBean;
import org.meanbean.factories.BasicNewObjectInstanceFactory;
import org.meanbean.factories.FactoryRepository;
import org.meanbean.factories.NoSuchFactoryException;
import org.meanbean.factories.basic.EnumFactory;
import org.meanbean.factories.basic.StringFactory;
import org.meanbean.factories.util.BasicFactoryLookupStrategy;
import org.meanbean.factories.util.FactoryLookupStrategy;
import org.meanbean.lang.Factory;
import org.meanbean.test.beans.NonBean;
import org.meanbean.test.beans.NullFactory;
import org.meanbean.util.RandomValueGenerator;
import org.meanbean.util.SimpleRandomValueGenerator;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BasicFactoryLookupStrategyTest {

  private static final String IRRELEVANT_PROPERTY_NAME = "IRRELEVANT";

  private static final String PROPERTY_NAME = "A PROPERTY";

  public enum Color {
    RED, BLUE, GREEN
  }



  public static class BasicBean {

  }

  private final BeanInformationFactory beanInformationFactory = new JavaBeanInformationFactory();

  private final BeanInformation beanInformationReal = beanInformationFactory.create(BasicBean.class);

  @Mock
  private BeanInformation beanInformationMock;

  private FactoryRepository factoryCollection;

  private FactoryLookupStrategy factoryLookupStrategy;

  private RandomValueGenerator randomValueGenerator;


  @Before
  public void before() {
    randomValueGenerator = new SimpleRandomValueGenerator();
    factoryCollection = new FactoryRepository();
    factoryLookupStrategy = new BasicFactoryLookupStrategy(factoryCollection, randomValueGenerator);

  }


  @Test(expected = IllegalArgumentException.class)
  public void constructorShouldPreventNullFactoryCollection() throws Exception {
    new BasicFactoryLookupStrategy(null, randomValueGenerator);
  }


  @Test(expected = IllegalArgumentException.class)
  public void constructorShouldPreventNullRandomValueGenerator() throws Exception {
    new BasicFactoryLookupStrategy(factoryCollection, null);
  }


  @Test(expected = IllegalArgumentException.class)
  public void getFactoryShouldPreventNullBeanInformation() throws Exception {
    factoryLookupStrategy.getFactory(null, new PropertyInformationBean(), null);
  }


  @Test(expected = IllegalArgumentException.class)
  public void getFactoryShouldPreventNullPropertyName() throws Exception {
    factoryLookupStrategy.getFactory(beanInformationMock, null, null);
  }

  // TODO TEST EQUIVALENT POPULATED BEAN FACTORY


  @Test
  public void getFactoryShouldReturnBasicNewObjectInstanceFactoryForUnrecognisedPropertyTypeThatIsSameAsParentBean()
      throws Exception {
    PropertyInformationBean propertyInformationBean = new PropertyInformationBean();
    propertyInformationBean.setName(IRRELEVANT_PROPERTY_NAME);
    propertyInformationBean.setReadMethodReturnType(BasicBean.class);
    Factory<?> factory = factoryLookupStrategy.getFactory(beanInformationReal, propertyInformationBean, null);
    assertThat("Incorrect factory.", factory.getClass().getName(),
        is(BasicNewObjectInstanceFactory.class.getName())
    );
  }


  @Test
  public void getFactoryShouldReturnEnumFactoryForEnumTypes() throws Exception {
    PropertyInformationBean propertyInformationBean = new PropertyInformationBean();
    propertyInformationBean.setName(IRRELEVANT_PROPERTY_NAME);
    propertyInformationBean.setReadMethodReturnType(Color.class);
    Factory<?> factory = factoryLookupStrategy.getFactory(beanInformationMock, propertyInformationBean, null);
    assertThat("Incorrect factory.", factory.getClass().getName(), is(EnumFactory.class.getName()));
  }


  @Test
  public void getFactoryShouldReturnFactoryInConfigurationRatherThanDynamicBeanFactory() throws Exception {
    Configuration configuration = new ConfigurationBuilder()
        .overrideFactory(PROPERTY_NAME, new NullFactory())
        .build();
    PropertyInformationBean propertyInformationBean = new PropertyInformationBean();
    propertyInformationBean.setName(PROPERTY_NAME);
    propertyInformationBean.setReadMethodReturnType(BasicBean.class);
    Factory<?> factory = factoryLookupStrategy.getFactory(beanInformationMock, propertyInformationBean, configuration);
    assertThat("Incorrect factory.", factory.getClass().getName(), is(NullFactory.class.getName()));
  }


  @Test
  public void getFactoryShouldReturnFactoryInConfigurationRatherThanEnumFactory() throws Exception {
    Configuration configuration = new ConfigurationBuilder()
        .overrideFactory(PROPERTY_NAME, new NullFactory())
        .build();
    PropertyInformationBean propertyInformationBean = new PropertyInformationBean();
    propertyInformationBean.setName(PROPERTY_NAME);
    propertyInformationBean.setReadMethodReturnType(Color.class);
    Factory<?> factory = factoryLookupStrategy.getFactory(beanInformationMock, propertyInformationBean, configuration);
    assertThat("Incorrect factory.", factory.getClass().getName(), is(NullFactory.class.getName()));
  }


  @Test
  public void getFactoryShouldReturnFactoryInConfigurationRatherThanRegisteredFactory() throws Exception {
    Configuration configuration = new ConfigurationBuilder()
        .overrideFactory(PROPERTY_NAME, new NullFactory())
        .build();
    PropertyInformationBean propertyInformationBean = new PropertyInformationBean();
    propertyInformationBean.setName(PROPERTY_NAME);
    propertyInformationBean.setReadMethodReturnType(String.class);
    Factory<?> factory = factoryLookupStrategy.getFactory(beanInformationMock, propertyInformationBean, configuration);
    assertThat("Incorrect factory.", factory.getClass().getName(), is(NullFactory.class.getName()));
  }


  @Test
  public void getFactoryShouldReturnRegisteredFactoryForRegisteredTypes() throws Exception {
    PropertyInformationBean propertyInformationBean = new PropertyInformationBean();
    propertyInformationBean.setName(IRRELEVANT_PROPERTY_NAME);
    propertyInformationBean.setReadMethodReturnType(String.class);
    Factory<?> factory = factoryLookupStrategy.getFactory(beanInformationMock, propertyInformationBean, null);
    assertThat("Incorrect factory.", factory.getClass().getName(), is(StringFactory.class.getName()));
  }


  @Test(expected = NoSuchFactoryException.class)
  public void getFactoryShouldThrowNoSuchFactoryExceptionForUnsupportedTypes() throws Exception {
    PropertyInformationBean propertyInformationBean = new PropertyInformationBean();
    propertyInformationBean.setName(IRRELEVANT_PROPERTY_NAME);
    propertyInformationBean.setReadMethodReturnType(NonBean.class);
    factoryLookupStrategy.getFactory(beanInformationReal, propertyInformationBean, null);
  }

}
