package io.setl.beantester.factories.basic;

import java.lang.reflect.Type;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;
import io.setl.beantester.factories.ValueFactory;

/**
 * FactoryLookup for EnumFactory instances
 */
public class EnumFactoryLookup implements FactoryLookup {

  private final RandomGenerator random;

  public EnumFactoryLookup(RandomGenerator random) {
    this.random = random;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ValueFactory<T> getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
    return (ValueFactory<T>) new EnumValueFactory((Class<?>) type, random);
  }


  @SuppressWarnings("rawtypes")
  @Override
  public boolean hasFactory(Type type) throws IllegalArgumentException {
    return type instanceof Class && ((Class) type).isEnum();
  }

}
