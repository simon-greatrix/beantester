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
  <T, S> VerifierSettingsEditor addEqualsInsignificantProperty(String propertyName);

  /**
   * Add a property that is insignificant for EqualsMethodTester
   *
   * <pre>
   *     addEqualsInsignificantProperty(MyBean::getPropertyValue);
   * </pre>
   */
  <T, S> VerifierSettingsEditor addEqualsInsignificantProperty(SerializableFunction1<T, S> beanGetter);

  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   * <pre>
   *     addIgnoredProperty(MyBean::getPropertyValue);
   * </pre>
   */
  <T, S> VerifierSettingsEditor addIgnoredProperty(SerializableFunction1<T, S> beanGetter) throws IllegalArgumentException;

  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   */
  VerifierSettingsEditor addIgnoredPropertyName(String property) throws IllegalArgumentException;

  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   */
  <T> VerifierSettingsEditor addOverrideFactory(String property, Factory<T> factory) throws IllegalArgumentException;

  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   * <pre>
   *     addOverridePropertyFactory(MyBean::getPropertyValue, () -&gt; createPropertyValue());
   * </pre>
   */
  <T, S> VerifierSettingsEditor addOverridePropertyFactory(SerializableFunction1<T, S> beanGetter, Factory<S> factory);

  /**
   * Finish editing setting and return to bean verification
   */
  BeanVerifier edited();

  <T> VerifierSettingsEditor registerFactory(Class<T> clazz, Factory<? extends T> factory);

  /**
   * Register factory for an inheritance type hierarchy
   */
  <T> VerifierSettingsEditor registerTypeHierarchyFactory(Class<T> baseType, Factory<T> factory);

  /** Set the bean information factory. */
  VerifierSettingsEditor setBeanInformationFactory(BeanInformationFactory beanInformationFactory);

  /**
   * Set the number of times a type should be tested by default
   */
  VerifierSettingsEditor setDefaultIterations(int iterations);

  VerifierSettingsEditor setFactoryCollection(FactoryCollection factoryCollection);

  VerifierSettingsEditor setFactoryLookupStrategy(FactoryLookupStrategy factoryLookupStrategy);

  VerifierSettingsEditor setRandomValueGenerator(RandomValueGenerator randomValueGenerator);

  /**
   * Suppress a warning.
   *
   * @param warning the warning to suppress.
   *
   * @return this
   */
  VerifierSettingsEditor suppressWarning(Warning warning);

}
