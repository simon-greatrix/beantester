package io.setl.beantester.factories;

import java.lang.reflect.Type;

import io.setl.beantester.ValueFactory;


/**
 * For looking up Factory instances.
 */
public interface FactoryLookup {

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
  ValueFactory getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException;


  /**
   * Check if this contains a Factory registered against the specified class.
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
