package io.setl.beantester.factories.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.factories.basic.BooleanValueFactory;
import io.setl.beantester.factories.basic.IntegerValueFactory;
import io.setl.beantester.factories.basic.LongValueFactory;

/** Load the "java.util.*" factories. */
public class UtilFactories {

  private static final UUID PRIMARY_UUID = UUID.fromString("00000000-0000-4000-8000-000000000000");

  private static final UUID SECONDARY_UUID = UUID.fromString("ffffffff-ffff-4fff-bfff-ffffffffffff");


  /**
   * Load the "java.util.*" factories.
   *
   * @param repository the repository to load the factories into
   */
  public static void load(FactoryRepository repository) {
    // Atomic wrappers
    ValueFactory intSource = new IntegerValueFactory();
    repository.addFactory(new ValueFactory(AtomicInteger.class, (t) -> new AtomicInteger((Integer) intSource.create(t))));

    ValueFactory longSource = new LongValueFactory();
    repository.addFactory(new ValueFactory(AtomicLong.class, (t) -> new AtomicLong((Long) longSource.create(t))));

    ValueFactory boolSource = new BooleanValueFactory();
    repository.addFactory(new ValueFactory(AtomicBoolean.class, (t) -> new AtomicBoolean((Boolean) boolSource.create(t))));

    // Locales
    repository.addFactory(new LocaleValueFactory());

    repository.addFactory(new ValueFactory(UUID.class, () -> PRIMARY_UUID, () -> SECONDARY_UUID, UUID::randomUUID));

    // Collections and optionals
    repository.addFactoryLookup(new CollectionFactoryLookup());
    repository.addFactoryLookup(new OptionalFactoryLookup());
  }

}
