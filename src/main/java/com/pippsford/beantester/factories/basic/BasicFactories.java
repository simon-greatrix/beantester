package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.FactoryRepository;

/** Load the basic factories into the factory repository. */
public class BasicFactories {

  /**
   * Load the basic factories.
   *
   * @param repository the repository to load the factories into
   */
  public static void load(FactoryRepository repository) {
    repository.addFactory(new BigDecimalValueFactory());
    repository.addFactory(new BigIntegerValueFactory());
    repository.addFactory(new BooleanValueFactory());
    repository.addFactory(new ByteValueFactory());
    repository.addFactory(new CharacterValueFactory());
    repository.addFactory(new DoubleValueFactory());
    repository.addFactory(new FloatValueFactory());
    repository.addFactory(new IntegerValueFactory());
    repository.addFactory(new LongValueFactory());
    repository.addFactory(new ShortValueFactory());
    repository.addFactory(new StringValueFactory());
    repository.addFactory(new NumberValueFactory());
    repository.addFactory(ValueFactory.VOID_CLASS_FACTORY);

    repository.addFactory(ValueFactory.VOID_TYPE_FACTORY);

    repository.addFactoryLookup(new PrimitiveFactoryLookup());
    repository.addFactoryLookup(new ArrayFactoryLookup());
    repository.addFactoryLookup(new EnumFactoryLookup());
  }

}
