package io.setl.beantester.factories.basic;

import java.lang.reflect.Type;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;

/**
 * FactoryLookup for EnumFactory instances.
 */
public class EnumFactoryLookup implements FactoryLookup {

  @Override
  public ValueFactory getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
    if (!(type instanceof Class<?> enumClass)) {
      throw new NoSuchFactoryException("Cannot create EnumFactory for non-Class type: " + type);
    }
    if (!enumClass.isEnum()) {
      throw new NoSuchFactoryException("Cannot create EnumFactory for non-Enum class: " + enumClass);
    }

    final Enum<?>[] enumConstants = (Enum<?>[]) enumClass.getEnumConstants();
    if (enumConstants.length < 2) {
      System.getLogger("EnumValueFactory").log(System.Logger.Level.WARNING, "Enum class has less than 2 constants. This may cause issues with some tests.");
    }
    Enum<?> primary = enumConstants[0];
    Enum<?> secondary = enumConstants[Math.min(1, enumConstants.length - 1)];
    return new ValueFactory(() -> primary, () -> secondary, () -> enumConstants[TestContext.get().getRandom().nextInt(enumConstants.length)]);
  }


  @SuppressWarnings("rawtypes")
  @Override
  public boolean hasFactory(Type type) throws IllegalArgumentException {
    return type instanceof Class && ((Class) type).isEnum();
  }

}
