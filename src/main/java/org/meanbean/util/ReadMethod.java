package org.meanbean.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public interface ReadMethod {

  /** Get the generic return type of the method. */
  Type getGenericReturnType();

  /** Get the method's name. */
  String getName();

  /**
   * Reads the value of the specified property from the specified object.
   *
   * @param target the object to read the property value from
   *
   * @return the value of the property
   */
  Object invoke(Object target) throws IllegalAccessException, InvocationTargetException;

}
