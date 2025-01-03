package io.setl.beantester.factories.basic;

import java.lang.reflect.Type;
import java.util.Optional;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryLookup;

/**
 * FactoryLookup for EnumFactory instances.
 */
public class EnumFactoryLookup implements FactoryLookup {

  @Override
  public Optional<ValueFactory> getFactory(Type type) {
    if (!(type instanceof Class<?> enumClass)) {
      return Optional.empty();
    }
    if (!enumClass.isEnum()) {
      return Optional.empty();
    }

    final Enum<?>[] enumConstants = (Enum<?>[]) enumClass.getEnumConstants();
    if (enumConstants.length < 2) {
      System.getLogger("EnumValueFactory").log(System.Logger.Level.WARNING, "Enum class has less than 2 constants. This may cause issues with some tests.");
    }
    Enum<?> primary = enumConstants[0];
    Enum<?> secondary = enumConstants[Math.min(1, enumConstants.length - 1)];
    return Optional.of(new ValueFactory(type, () -> primary, () -> secondary,
        () -> enumConstants[TestContext.get().getRandom().nextInt(enumConstants.length)]
    ));
  }

}
