package io.setl.beantester.factories.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.setl.beantester.factories.Factory;
import java.util.random.RandomGenerator;

public class LocaleFactory implements Factory<Locale>, FactoryCollectionPlugin {

  private final RandomGenerator randomValueGenerator = RandomGenerator.getInstance();

  private final RandomValueSampler randomValueSampler = new RandomValueSampler(randomValueGenerator);


  @Override
  public Locale create() {
    List<Locale> locales = Arrays.asList(Locale.getAvailableLocales());
    return randomValueSampler.getFrom(locales);
  }


  @Override
  public void initialize(FactoryCollection factoryCollection, RandomGenerator randomValueGenerator) {
    factoryCollection.addFactory(Locale.class, this);
  }

}
