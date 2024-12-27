package io.setl.beantester.factories.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.Sampler;

/** A factory for Locale instances. */
public class LocaleValueFactory extends ValueFactory {

  private static final List<Locale> locales = Arrays.asList(Locale.getAvailableLocales());


  public LocaleValueFactory() {
    super(Locale.class, () -> Locale.UK, () -> Locale.FRANCE, () -> Sampler.getFrom(locales));
  }

}
