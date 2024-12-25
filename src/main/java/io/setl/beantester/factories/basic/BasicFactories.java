package io.setl.beantester.factories.basic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.ValueFactoryRepository;

/** Load the basic factories. */
public class BasicFactories {

  private static final UUID PRIMARY_UUID = UUID.fromString("00000000-0000-4000-8000-000000000000");

  private static final UUID SECONDARY_UUID = UUID.fromString("ffffffff-ffff-4fff-bfff-ffffffffffff");


  /**
   * Load the basic factories.
   *
   * @param context    the test context
   * @param repository the repository to load the factories into
   */
  public static void load(ValueFactoryRepository repository) {
    repository.addFactory(BigDecimal.class, new BigDecimalValueFactory());
    repository.addFactory(BigInteger.class, new BigIntegerValueFactory());
    repository.addFactory(Boolean.class, new BooleanValueFactory());
    repository.addFactory(Byte.class, new ByteValueFactory());
    repository.addFactory(Character.class, new CharacterValueFactory());
    repository.addFactory(Double.class, new DoubleValueFactory());
    repository.addFactory(Float.class, new FloatValueFactory());
    repository.addFactory(Integer.class, new IntegerValueFactory());
    repository.addFactory(Long.class, new LongValueFactory());
    repository.addFactory(Short.class, new ShortValueFactory());
    repository.addFactory(String.class, new StringValueFactory());
    repository.addFactory(Void.TYPE, new ValueFactory(()->null,()->null,()->null));
    repository.addFactory(UUID.class, new ValueFactory(()->PRIMARY_UUID,()->SECONDARY_UUID,()->UUID.randomUUID()));

    repository.addFactory(boolean.class, new BooleanValueFactory());
    repository.addFactory(byte.class, new ByteValueFactory());
    repository.addFactory(short.class, new ShortValueFactory());
    repository.addFactory(int.class, new IntegerValueFactory());
    repository.addFactory(long.class, new LongValueFactory());
    repository.addFactory(float.class, new FloatValueFactory());
    repository.addFactory(double.class, new DoubleValueFactory());
    repository.addFactory(char.class, new CharacterValueFactory());
    repository.addFactory(void.class, new ValueFactory(()->null,()->null,()->null));

    repository.addFactoryLookup(new ArrayFactoryLookup());
    repository.addFactoryLookup(new EnumFactoryLookup());
  }

}
