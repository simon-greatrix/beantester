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

import static org.meanbean.test.Configuration.customConfigurationProvider;
import static org.meanbean.util.PropertyNameFinder.findPropertyName;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import org.meanbean.bean.info.BeanInformationFactory;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.FactoryLookup;
import org.meanbean.factories.NoSuchFactoryException;
import org.meanbean.factories.util.FactoryLookupStrategy;
import org.meanbean.lang.Factory;
import org.meanbean.mirror.SerializableLambdas.SerializableFunction1;
import org.meanbean.util.RandomValueGenerator;
import org.meanbean.util.ServiceFactory;
import org.meanbean.util.ValidationHelper;

/**
 * Builder for {@link BeanTester} or other tester classes.
 * <br>
 * <b>Prefer {@link BeanVerifier}</b>
 */
public class BeanTesterBuilder {

  public static BeanTester newBeanTester() {
    return new BeanTesterBuilder().build();
  }


  public static BeanTesterBuilder newBeanTesterBuilder() {
    return new BeanTesterBuilder();
  }


  static BeanTesterBuilder newBeanTesterBuilderWithInheritedContext() {
    return new BeanTesterBuilder(ServiceFactory::createContextIfNeeded);
  }


  public static EqualsMethodTester newEqualsMethodTester() {
    return new BeanTesterBuilder().buildEqualsMethodTester();
  }


  public static HashCodeMethodTester newHashCodeMethodTester() {
    return new HashCodeMethodTester();
  }


  public static ToStringMethodTester newToStringMethodTester() {
    return new ToStringMethodTester(Configuration.defaultConfigurationProvider());
  }

  private final Map<Class<?>, Configuration> customConfigurations;

  private final Configuration defaultConfiguration;

  private BeanInformationFactory beanInformationFactory;

  private BeanPropertyTester beanPropertyTester;

  private FactoryCollection factoryCollection;

  private FactoryLookupStrategy factoryLookupStrategy;

  private RandomValueGenerator randomValueGenerator;


  private BeanTesterBuilder() {
    this(ServiceFactory::createContext);
  }


  private BeanTesterBuilder(Consumer<BeanTesterBuilder> contextCreator) {
    contextCreator.accept(this);
    randomValueGenerator = RandomValueGenerator.getInstance();
    factoryCollection = FactoryCollection.getInstance();
    factoryLookupStrategy = FactoryLookupStrategy.getInstance();
    beanInformationFactory = BeanInformationFactory.getInstance();
    beanPropertyTester = new BeanPropertyTester();
    customConfigurations = new ConcurrentHashMap<>();
    defaultConfiguration = Configuration.defaultConfiguration();
  }


  /**
   * Add a property that is insignificant for EqualsMethodTester
   */
  public <T, S> BeanTesterBuilder addEqualsInsignificantProperty(Class<T> beanClass, String propertyName) {
    ValidationHelper.ensureExists("beanClass", "add equals insignificantProperty", beanClass);
    ValidationHelper.ensureExists("propertyName", "add equals insignificantProperty", propertyName);
    getConfigurationFor(beanClass).getEqualsInsignificantProperties().add(propertyName);
    return this;
  }


  /**
   * Add a property that is insignificant for EqualsMethodTester
   *
   * <pre>
   *     addEqualsInsignificantProperty(MyBean.class, MyBean::getPropertyValue);
   * </pre>
   */
  public <T, S> BeanTesterBuilder addEqualsInsignificantProperty(Class<T> beanClass, SerializableFunction1<T, S> beanGetter) {
    ValidationHelper.ensureExists("beanClass", "add equals insignificantProperty", beanClass);
    ValidationHelper.ensureExists("beanGetter", "add equals insignificantProperty", beanGetter);

    String propertyName = findPropertyName(beanClass, beanGetter);
    return addEqualsInsignificantProperty(beanClass, propertyName);
  }


  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   * <pre>
   *     addIgnoredProperty(MyBean.class, MyBean::getPropertyValue);
   * </pre>
   */
  public <T, S> BeanTesterBuilder addIgnoredProperty(Class<T> beanClass, SerializableFunction1<T, S> beanGetter)
      throws IllegalArgumentException {
    String propertyName = findPropertyName(beanClass, beanGetter);
    return addIgnoredPropertyName(beanClass, propertyName);
  }


  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   */
  public BeanTesterBuilder addIgnoredPropertyName(Class<?> beanClass, String property) throws IllegalArgumentException {
    ValidationHelper.ensureExists("property", "add property to ignored properties collection", property);
    getConfigurationFor(beanClass).getIgnoredProperties().add(property);
    return this;
  }


  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   */
  public <T> BeanTesterBuilder addOverrideFactory(Class<T> beanClass, String property, Factory<T> factory)
      throws IllegalArgumentException {
    ValidationHelper.ensureExists("beanClass", "add override Factory", beanClass);
    ValidationHelper.ensureExists("property", "add override Factory", property);
    ValidationHelper.ensureExists("factory", "add override Factory", factory);
    getConfigurationFor(beanClass).getOverrideFactories().put(property, factory);
    return this;
  }


  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   * <pre>
   *     addOverridePropertyFactory(MyBean.class, MyBean::getPropertyValue, () -&gt; createPropertyValue());
   * </pre>
   */
  public <T, S> BeanTesterBuilder addOverridePropertyFactory(
      Class<T> beanClass, SerializableFunction1<T, S> beanGetter,
      Factory<S> factory
  )
      throws IllegalArgumentException {
    ValidationHelper.ensureExists("beanClass", "add override Factory", beanClass);
    ValidationHelper.ensureExists("beanGetter", "add override Factory", beanGetter);
    ValidationHelper.ensureExists("factory", "add override Factory", factory);

    String propertyName = findPropertyName(beanClass, beanGetter);
    getConfigurationFor(beanClass).getOverrideFactories().put(propertyName, factory);
    return this;
  }


  public BeanTester build() {
    return new BeanTester(
        randomValueGenerator,
        factoryCollection,
        factoryLookupStrategy,
        beanInformationFactory,
        beanPropertyTester,
        createConfigurationProvider()
    );
  }


  public EqualsMethodTester buildEqualsMethodTester() {
    return EqualsMethodTester.createWithInheritedContext(createConfigurationProvider());
  }


  public HashCodeMethodTester buildHashCodeMethodTester() {
    return HashCodeMethodTester.createWithInheritedContext(createConfigurationProvider());
  }


  public ToStringMethodTester buildToStringMethodTester() {
    return ToStringMethodTester.createWithInheritedContext(createConfigurationProvider());
  }


  private Function<Class<?>, Configuration> createConfigurationProvider() {
    return customConfigurationProvider(customConfigurations, defaultConfiguration);
  }


  public BeanInformationFactory getBeanInformationFactory() {
    return beanInformationFactory;
  }


  public BeanPropertyTester getBeanPropertyTester() {
    return beanPropertyTester;
  }


  Configuration getConfigurationFor(Class<?> clazz) {
    return customConfigurations.computeIfAbsent(
        clazz,
        key -> Configuration.defaultMutableConfiguration(defaultConfiguration.getIterations())
    );
  }


  public int getDefaultIterations() {
    return defaultConfiguration.getIterations();
  }


  public FactoryCollection getFactoryCollection() {
    return factoryCollection;
  }


  public FactoryLookupStrategy getFactoryLookupStrategy() {
    return factoryLookupStrategy;
  }


  public int getIterations(Class<?> beanClass) {
    return getConfigurationFor(beanClass).getIterations();
  }


  public RandomValueGenerator getRandomValueGenerator() {
    return randomValueGenerator;
  }


  /**
   * Register a custom factory for given class
   */
  public <T> BeanTesterBuilder registerFactory(Class<T> clazz, Factory<? extends T> factory) {
    getFactoryCollection().addFactory(clazz, factory);
    return this;
  }


  /**
   * Register factory for an inheritance type hierarchy
   */
  public <T> BeanTesterBuilder registerTypeHierarchyFactory(Class<T> baseType, Factory<T> factory) {
    getFactoryCollection().addFactoryLookup(new FactoryLookup() {

      @SuppressWarnings("unchecked")
      @Override
      public <E> Factory<E> getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
        if (hasFactory(type)) {
          return (Factory<E>) factory;
        }
        throw new NoSuchFactoryException("No factory for " + type);
      }


      @Override
      public boolean hasFactory(Type type) throws IllegalArgumentException {
        return type instanceof Class && baseType.isAssignableFrom((Class<?>) type);
      }
    });
    return this;
  }


  public BeanTesterBuilder setBeanInformationFactory(BeanInformationFactory beanInformationFactory) {
    this.beanInformationFactory = beanInformationFactory;
    return this;
  }


  public BeanTesterBuilder setBeanPropertyTester(BeanPropertyTester beanPropertyTester) {
    this.beanPropertyTester = beanPropertyTester;
    return this;
  }


  /**
   * Set the number of times a type should be tested by default
   */
  public BeanTesterBuilder setDefaultIterations(int iterations) {
    this.defaultConfiguration.setIterations(iterations);
    return this;
  }


  public BeanTesterBuilder setFactoryCollection(FactoryCollection factoryCollection) {
    this.factoryCollection = factoryCollection;
    return this;
  }


  public BeanTesterBuilder setFactoryLookupStrategy(FactoryLookupStrategy factoryLookupStrategy) {
    this.factoryLookupStrategy = factoryLookupStrategy;
    return this;
  }


  public BeanTesterBuilder setIterations(Class<?> beanClass, int num) {
    getConfigurationFor(beanClass).setIterations(num);
    return this;
  }


  public BeanTesterBuilder setRandomValueGenerator(RandomValueGenerator randomValueGenerator) {
    this.randomValueGenerator = randomValueGenerator;
    return this;
  }

}
