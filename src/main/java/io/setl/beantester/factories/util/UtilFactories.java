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
import io.setl.beantester.factories.basic.BooleanValueFactory;
import io.setl.beantester.factories.basic.IntegerValueFactory;
import io.setl.beantester.factories.basic.LongValueFactory;

public class UtilFactories {

  public static void load(TestContext context, ValueFactoryRepository repository) {
    RandomGenerator random = context.getRandom();

    // Atomic wrappers
    repository.addFactory(AtomicInteger.class, newFactory(new IntegerValueFactory(random), AtomicInteger::new));
    repository.addFactory(AtomicLong.class, newFactory(new LongValueFactory(random), AtomicLong::new));
    repository.addFactory(AtomicBoolean.class, newFactory(new BooleanValueFactory(random), AtomicBoolean::new));

    // Locales
    repository.addFactory(Locale.class, new LocaleValueFactory(context.getRandom()));

    // Collections and optionals
    repository.addFactoryLookup(new CollectionFactoryLookup(context));
    repository.addFactoryLookup(new OptionalFactoryLookup(context));
  }


  private static <A, N> ValueFactory<A> newFactory(ValueFactory<N> valueFactory, Function<N, A> fn) {
    return (t) -> fn.apply(valueFactory.create(t));
  }

}
