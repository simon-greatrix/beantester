package io.setl.beantester.factories.util;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.ValueFactory;
import io.setl.beantester.factories.ValueFactoryRepository;

public class UtilFactories {

  public static void load(TestContext context, ValueFactoryRepository repository) {
    RandomGenerator random = context.getRandom();

    // Atomic wrappers
    repository.addFactory(AtomicInteger.class, newFactory(random::nextInt, AtomicInteger::new));
    repository.addFactory(AtomicLong.class, newFactory(random::nextLong, AtomicLong::new));
    repository.addFactory(AtomicBoolean.class, () -> new AtomicBoolean(random.nextBoolean()));

    // Locales
    repository.addFactory(Locale.class, () -> new LocaleValueFactory(context.getRandom()));

    // Collections and optionals
    repository.addFactoryLookup(new CollectionFactoryLookup(context));
    repository.addFactoryLookup(new OptionalFactoryLookup(context));
  }


  private static <A extends Number, N extends Number> ValueFactory<A> newFactory(ValueFactory<N> valueFactory, Function<N, A> fn) {
    return () -> fn.apply(valueFactory.create());
  }

}
