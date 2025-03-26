package com.pippsford.beantester.factories.basic;

import java.lang.reflect.Type;
import java.util.Optional;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.FactoryLookup;

/**
 * FactoryLookup for EnumFactory instances.
 */
public class EnumFactoryLookup implements FactoryLookup {

  /**
   * Create a factory for the Enum type. Note that it is possible to define an Enum with zero or only one member.
   *
   * @param type The type created by the factory.
   *
   * @return a factory.
   */
  @Override
  public Optional<ValueFactory> getFactory(Type type) {
    if (!(type instanceof Class<?> enumClass)) {
      return Optional.empty();
    }
    if (!enumClass.isEnum()) {
      return Optional.empty();
    }

    final Enum<?>[] enumConstants = (Enum<?>[]) enumClass.getEnumConstants();

    // Zero constants is a special case, as it is not possible to create a value for the enum.
    if (enumConstants.length == 0) {
      return Optional.of(ValueFactory.mandatoryNull(enumClass));
    }

    // A single constant means every value will be the same, this prevents testing that changing a value really changes the value.
    if (enumConstants.length == 1) {
      System.getLogger("EnumValueFactory").log(System.Logger.Level.WARNING, "Enum class has less than 2 constants. This may cause issues with some tests.");
    }

    // Create a factory
    Enum<?> primary = enumConstants[0];
    Enum<?> secondary = enumConstants[Math.min(1, enumConstants.length - 1)];
    return Optional.of(new ValueFactory(
        type, () -> primary, () -> secondary,
        () -> enumConstants[TestContext.get().getRandom().nextInt(enumConstants.length)]
    ));
  }

}
