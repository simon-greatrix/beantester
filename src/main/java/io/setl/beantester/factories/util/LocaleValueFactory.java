package io.setl.beantester.factories.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.Sampler;
import io.setl.beantester.factories.ValueFactory;
import io.setl.beantester.factories.ValueType;

/** A factory for Locale instances. */
public class LocaleValueFactory implements ValueFactory<Locale> {

  private final List<Locale> locales = Arrays.asList(Locale.getAvailableLocales());

  private final RandomGenerator random;


  public LocaleValueFactory(RandomGenerator random) {
    this.random = random;
  }


  @Override
  public Locale create(ValueType type) {
    if (type == ValueType.PRIMARY) {
      return Locale.UK;
    }
    if (type == ValueType.SECONDARY) {
      return Locale.FRANCE;
    }
    return Sampler.getFrom(random, locales);
  }

}
