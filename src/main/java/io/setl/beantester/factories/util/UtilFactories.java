package io.setl.beantester.factories.util;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.ValueFactoryRepository;
import io.setl.beantester.factories.basic.BooleanValueFactory;
import io.setl.beantester.factories.basic.IntegerValueFactory;
import io.setl.beantester.factories.basic.LongValueFactory;

/** Load the "java.util.*" factories. */
public class UtilFactories {

  /**
   * Load the "java.util.*" factories.
   *
   * @param repository the repository to load the factories into
   */
  public static void load(ValueFactoryRepository repository) {
    // Atomic wrappers
    ValueFactory intSource = new IntegerValueFactory();
    repository.addFactory(AtomicInteger.class, new ValueFactory((t) -> new AtomicInteger((Integer) intSource.create(t))));

    ValueFactory longSource = new LongValueFactory();
    repository.addFactory(AtomicLong.class, new ValueFactory((t) -> new AtomicLong((Long) longSource.create(t))));

    ValueFactory boolSource = new BooleanValueFactory();
    repository.addFactory(AtomicBoolean.class, new ValueFactory((t) -> new AtomicBoolean((Boolean) boolSource.create(t))));

    // Locales
    repository.addFactory(Locale.class, new LocaleValueFactory());

    // Collections and optionals
    repository.addFactoryLookup(new CollectionFactoryLookup());
    repository.addFactoryLookup(new OptionalFactoryLookup());
  }

}
