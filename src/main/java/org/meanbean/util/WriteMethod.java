package org.meanbean.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * A method that updates the state of an object.
 */
public interface WriteMethod {

  /** Get the value type written by this method. */
  Type getGenericValueType();

  /** Get the method's name. */
  String getName();

  /**
   * Either updates the provided object, or returns a new object with the updated state.
   *
   * @param target the object to update (or create a copy of)
   * @param value  the new value to set
   *
   * @return the updated object, or a new object identical except for the updated value
   */
  Object invoke(Object target, Object value) throws InvocationTargetException, IllegalAccessException;

}
