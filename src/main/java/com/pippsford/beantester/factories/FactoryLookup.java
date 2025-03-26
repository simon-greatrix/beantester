package com.pippsford.beantester.factories;

import java.lang.reflect.Type;
import java.util.Optional;

import com.pippsford.beantester.ValueFactory;


/**
 * For looking up Factory instances.
 */
public interface FactoryLookup {


  /**
   * Get or create the Factory for the specified property.
   *
   * @param beanClass    The class of the bean that needs a value
   * @param propertyName The name of the property that needs a value
   * @param propertyType The type of the property that needs a value
   *
   * @return The requested Factory, or an empty optional if the factory cannot be created by this FactoryLookup.
   */
  default Optional<ValueFactory> getFactory(Class<?> beanClass, String propertyName, Type propertyType) {
    return Optional.empty();
  }


  /**
   * Get or create the Factory for the specified type.
   *
   * @param type The type created by the factory.
   *
   * @return The requested Factory, or an empty optional if the factory cannot be created by this FactoryLookup.
   */
  default Optional<ValueFactory> getFactory(Type type) {
    return Optional.empty();
  }

}
