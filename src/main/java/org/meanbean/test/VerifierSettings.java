package org.meanbean.test;

import org.meanbean.bean.info.BeanInformationFactory;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.util.FactoryLookupStrategy;
import org.meanbean.lang.Factory;
import org.meanbean.mirror.SerializableLambdas.SerializableFunction1;
import org.meanbean.util.RandomValueGenerator;

/**
 * @see BeanVerifier
 * @see BeanTesterBuilder
 * @see Configuration
 */
public interface VerifierSettings {

  /**
   * Add a property that is insignificant for EqualsMethodTester
   */
  <T, S> VerifierSettings addEqualsInsignificantProperty(String propertyName);

  /**
   * Add a property that is insignificant for EqualsMethodTester
   *
   * <pre>
   *     addEqualsInsignificantProperty(MyBean::getPropertyValue);
   * </pre>
   */
  <T, S> VerifierSettings addEqualsInsignificantProperty(SerializableFunction1<T, S> beanGetter);

  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   * <pre>
   *     addIgnoredProperty(MyBean::getPropertyValue);
   * </pre>
   */
  <T, S> VerifierSettings addIgnoredProperty(SerializableFunction1<T, S> beanGetter) throws IllegalArgumentException;

  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   */
  VerifierSettings addIgnoredPropertyName(String property) throws IllegalArgumentException;

  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   */
  <T> VerifierSettings addOverrideFactory(String property, Factory<T> factory) throws IllegalArgumentException;

  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   * <pre>
   *     addOverridePropertyFactory(MyBean::getPropertyValue, () -&gt; createPropertyValue());
   * </pre>
   */
  <T, S> VerifierSettings addOverridePropertyFactory(SerializableFunction1<T, S> beanGetter, Factory<S> factory);

  BeanInformationFactory getBeanInformationFactory();

  int getDefaultIterations();

  FactoryCollection getFactoryCollection();

  FactoryLookupStrategy getFactoryLookupStrategy();

  RandomValueGenerator getRandomValueGenerator();

  /**
   * Register a custom factory for given class
   */
  <T> VerifierSettings registerFactory(Class<T> clazz, Factory<? extends T> factory);

  /**
   * Register factory for an inheritance type hierarchy
   */
  <T> VerifierSettings registerTypeHierarchyFactory(Class<T> baseType, Factory<T> factory);

  VerifierSettings setBeanInformationFactory(BeanInformationFactory beanInformationFactory);

  /**
   * Set the number of times a type should be tested by default
   */
  VerifierSettings setDefaultIterations(int iterations);

  VerifierSettings setFactoryCollection(FactoryCollection factoryCollection);

  VerifierSettings setFactoryLookupStrategy(FactoryLookupStrategy factoryLookupStrategy);

  VerifierSettings setRandomValueGenerator(RandomValueGenerator randomValueGenerator);

  VerifierSettingsEditor suppressWarning(Warning warning);

}
