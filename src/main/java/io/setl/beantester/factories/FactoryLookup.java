package io.setl.beantester.factories;

import java.lang.reflect.Type;
import java.util.function.Supplier;

import org.meanbean.util.ServiceDefinition;

/**
 * For looking up Factory instances
 */
public interface FactoryLookup {

  static FactoryLookup getInstance() {
    return getServiceDefinition().getServiceFactory()
        .getFirst();
  }

  static ServiceDefinition<FactoryLookup> getServiceDefinition() {
    return new ServiceDefinition<>(FactoryLookup.class);
  }

  /**
   * <p>
   * Get the Factory registered for the specified class.
   * </p>
   *
   * <p>
   * To check whether a Factory is registered for a specified class, please refer to
   * <code>hasFactory(Class&lt;?&gt; clazz);</code>.
   * </p>
   *
   * @param type The type the Factory is registered against. This should be the type of object that the Factory
   *             creates.
   *
   * @return The requested Factory.
   *
   * @throws IllegalArgumentException If the class is deemed illegal.
   * @throws NoSuchFactoryException   If this does not contain a Factory registered against the specified class.
   */
  <T> Factory<T> getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException;

  default <T> Factory<T> getFactoryIfAvailable(Type type, Supplier<Factory<T>> fallback) {
    if (hasFactory(type)) {
      return getFactory(type);
    }
    return fallback.get();
  }

  /**
   * Does this contain a Factory registered against the specified class?
   *
   * @param type The type a Factory could be registered against. This should be the type of object that the Factory
   *             creates.
   *
   * @return <code>true</code> if the collection contains a Factory registered for the specified class;
   *     <code>false</code> otherwise.
   *
   * @throws IllegalArgumentException If the clazz is deemed illegal.
   */
  boolean hasFactory(Type type) throws IllegalArgumentException;

}
