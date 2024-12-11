package io.setl.beantester.factories;

/**
 * Defines an object that creates objects of a specified type.
 *
 * @param <T> The data type of the object this Factory creates.
 *
 * @author Graham Williamson
 */
public interface ValueFactory<T> {

  /**
   * Create a new object of the specified type.
   *
   * @return A new object of the specified type.
   */
  T create();

}
