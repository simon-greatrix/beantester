package io.setl.beantester.factories.basic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.ValueFactoryRepository;

public class BasicFactories {

  private static final UUID PRIMARY_UUID = UUID.fromString("00000000-0000-4000-8000-000000000000");

  private static final UUID SECONDARY_UUID = UUID.fromString("ffffffff-ffff-4fff-bfff-ffffffffffff");


  public static void load(TestContext context, ValueFactoryRepository repository) {
    RandomGenerator random = context.getRandom();

    repository.addFactory(BigDecimal.class, new BigDecimalValueFactory(random));
    repository.addFactory(BigInteger.class, new BigIntegerValueFactory(random));
    repository.addFactory(Boolean.class, new BooleanValueFactory(random));
    repository.addFactory(Byte.class, new ByteValueFactory(random));
    repository.addFactory(Character.class, new CharacterValueFactory(random));
    repository.addFactory(Double.class, new DoubleValueFactory(random));
    repository.addFactory(Float.class, new FloatValueFactory(random));
    repository.addFactory(Integer.class, new IntegerValueFactory(random));
    repository.addFactory(Long.class, new LongValueFactory(random));
    repository.addFactory(Short.class, new ShortValueFactory(random));
    repository.addFactory(String.class, new StringValueFactory(random));
    repository.addFactory(Void.TYPE, (t) -> null);
    repository.addFactory(UUID.class, (t) -> switch (t) {
      case PRIMARY -> PRIMARY_UUID;
      case SECONDARY -> SECONDARY_UUID;
      default -> UUID.randomUUID();
    });

    repository.addFactory(boolean.class, new BooleanValueFactory(random));
    repository.addFactory(byte.class, new ByteValueFactory(random));
    repository.addFactory(short.class, new ShortValueFactory(random));
    repository.addFactory(int.class, new IntegerValueFactory(random));
    repository.addFactory(long.class, new LongValueFactory(random));
    repository.addFactory(float.class, new FloatValueFactory(random));
    repository.addFactory(double.class, new DoubleValueFactory(random));
    repository.addFactory(char.class, new CharacterValueFactory(random));
    repository.addFactory(void.class, (t) -> null);

    repository.addFactoryLookup(new ArrayFactoryLookup(context));
    repository.addFactoryLookup(new EnumFactoryLookup(random));
  }

}
