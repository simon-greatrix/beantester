package org.meanbean.test;

import org.meanbean.bean.info.BeanInformationFactory;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.util.FactoryLookupStrategy;
import org.meanbean.util.RandomValueGenerator;

/**
 * @see BeanVerifier
 * @see BeanTesterBuilder
 * @see Configuration
 */
public interface VerifierSettings {

  BeanVerifier build();

  VerifierSettings copy();

  VerifierSettingsEditor edit();

  BeanInformationFactory getBeanInformationFactory();

  int getDefaultIterations();

  FactoryCollection getFactoryCollection();

  FactoryLookupStrategy getFactoryLookupStrategy();

  RandomValueGenerator getRandomValueGenerator();

}
