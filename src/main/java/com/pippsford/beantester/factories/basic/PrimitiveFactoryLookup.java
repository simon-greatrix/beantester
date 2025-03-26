package com.pippsford.beantester.factories.basic;

import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.FactoryLookup;
import com.pippsford.beantester.factories.NoSuchFactoryException;

/**
 * A lookup for factories handling primitive types.
 */
public class PrimitiveFactoryLookup implements FactoryLookup {


  @Override
  public Optional<ValueFactory> getFactory(Type type) throws NoSuchFactoryException {
    if (!(type instanceof Class<?> clazz)) {
      return Optional.empty();
    }

    // Need to convert from primitive to wrapper class. Java does not provide an obvious way to do this, so we use MethodType.
    MethodType methodType = MethodType.methodType(clazz);
    if (!methodType.hasPrimitives()) {
      return Optional.empty();
    }

    // Use MethodType to convert the primitive to its wrapper class, and get the factory for that
    return Optional.of(TestContext.get().getFactories().getFactory(methodType.wrap().returnType()));
  }

}
