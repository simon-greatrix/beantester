package io.setl.beantester.factories;

import java.lang.reflect.Type;
import java.util.Optional;

import io.setl.beantester.ValueFactory;


/**
 * For looking up Factory instances.
 */
public interface FactoryLookup {

  /**
   * Get or create the Factory for the specified type.
   *
   * @param type The type created by the factory.
   *
   * @return The requested Factory, or an empty optional if the factory cannot be created by this FactoryLookup.
   */
  Optional<ValueFactory> getFactory(Type type);

}
