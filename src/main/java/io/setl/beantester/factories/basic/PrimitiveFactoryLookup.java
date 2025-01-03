package io.setl.beantester.factories.basic;

import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.Optional;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;

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

    return Optional.of(TestContext.get().getFactories().getFactory(methodType.wrap().returnType()));
  }

}
