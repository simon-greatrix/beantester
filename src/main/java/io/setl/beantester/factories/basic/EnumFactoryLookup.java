package io.setl.beantester.factories.basic;

import java.lang.reflect.Type;

import io.setl.beantester.factories.Factory;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;

/**
 * FactoryLookup for EnumFactory instances
 */
public class EnumFactoryLookup implements FactoryLookup {

  @SuppressWarnings("unchecked")
  @Override
  public <T> Factory<T> getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
    return (Factory<T>) new EnumFactory((Class<?>) type, RandomFactoryBase.newRandom());
  }


  @SuppressWarnings("rawtypes")
  @Override
  public boolean hasFactory(Type type) throws IllegalArgumentException {
    return type instanceof Class && ((Class) type).isEnum();
  }

}
