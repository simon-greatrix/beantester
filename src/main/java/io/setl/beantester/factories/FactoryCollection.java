package io.setl.beantester.factories;

import org.meanbean.util.ServiceDefinition;

/**
 * Defines a collection factories of different types of objects.
 *
 * @author Graham Williamson
 */
public interface FactoryCollection extends FactoryLookup {

  static FactoryCollection getInstance() {
    return getServiceDefinition().getServiceFactory()
        .getFirst();
  }

  static ServiceDefinition<FactoryCollection> getServiceDefinition() {
    return new ServiceDefinition<>(FactoryCollection.class);
  }

  /**
   * <p>
   * Add the specified Factory to the collection.
   * </p>
   *
   * <p>
   * If a Factory is already registered against the specified class, the existing registered Factory will be replaced
   * with the Factory you specify here.
   * </p>
   *
   * @param clazz   The type of objects the Factory creates. The class type will be used to generate a key with which the
   *                Factory can be retrieved from the collection at a later stage.
   * @param factory The Factory to add to the collection.
   *
   * @throws IllegalArgumentException If either of the required parameters are deemed illegal.
   */
  void addFactory(Class<?> clazz, Factory<?> factory) throws IllegalArgumentException;

  /**
   * <p>
   * Add the specified FactoryLookup
   * </p>
   * <p>
   * The factoryLookup will be first consulted when looking for a Factory
   */
  void addFactoryLookup(FactoryLookup factoryLookup);

}
