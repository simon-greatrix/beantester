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
public interface VerifierSettingsEditor extends VerifierSettings {

  /**
   * Add a property that is insignificant for EqualsMethodTester
   */
  @Override
  <T, S> VerifierSettingsEditor addEqualsInsignificantProperty(String propertyName);

  /**
   * Add a property that is insignificant for EqualsMethodTester
   *
   * <pre>
   *     addEqualsInsignificantProperty(MyBean::getPropertyValue);
   * </pre>
   */
  @Override
  <T, S> VerifierSettingsEditor addEqualsInsignificantProperty(SerializableFunction1<T, S> beanGetter);

  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   * <pre>
   *     addIgnoredProperty(MyBean::getPropertyValue);
   * </pre>
   */
  @Override
  <T, S> VerifierSettingsEditor addIgnoredProperty(SerializableFunction1<T, S> beanGetter) throws IllegalArgumentException;

  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   */
  @Override
  VerifierSettingsEditor addIgnoredPropertyName(String property) throws IllegalArgumentException;

  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   */
  @Override
  <T> VerifierSettingsEditor addOverrideFactory(String property, Factory<T> factory) throws IllegalArgumentException;

  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   * <pre>
   *     addOverridePropertyFactory(MyBean::getPropertyValue, () -&gt; createPropertyValue());
   * </pre>
   */
  @Override
  <T, S> VerifierSettingsEditor addOverridePropertyFactory(SerializableFunction1<T, S> beanGetter, Factory<S> factory);

  /**
   * Finish editing setting and return to bean verification
   */
  BeanVerifier edited();

  @Override
  <T> VerifierSettingsEditor registerFactory(Class<T> clazz, Factory<? extends T> factory);

  /**
   * Register factory for an inheritance type hierarchy
   */
  @Override
  <T> VerifierSettingsEditor registerTypeHierarchyFactory(Class<T> baseType, Factory<T> factory);

  @Override
  VerifierSettingsEditor setBeanInformationFactory(BeanInformationFactory beanInformationFactory);

  /**
   * Set the number of times a type should be tested by default
   */
  @Override
  VerifierSettingsEditor setDefaultIterations(int iterations);

  @Override
  VerifierSettingsEditor setFactoryCollection(FactoryCollection factoryCollection);

  @Override
  VerifierSettingsEditor setFactoryLookupStrategy(FactoryLookupStrategy factoryLookupStrategy);

  @Override
  VerifierSettingsEditor setRandomValueGenerator(RandomValueGenerator randomValueGenerator);

}
