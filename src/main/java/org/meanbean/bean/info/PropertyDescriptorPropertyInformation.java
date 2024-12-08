/*-
 * ​​​
 * meanbean
 * ⁣⁣⁣
 * Copyright (C) 2010 - 2020 the original author or authors.
 * ⁣⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ﻿﻿﻿﻿﻿
 */

package org.meanbean.bean.info;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.meanbean.util.ReadMethod;
import org.meanbean.util.WriteMethod;
import org.meanbean.util.reflect.ReflectionAccessor;

/**
 * Concrete implementation of PropertyInformation that provides information about a JavaBean property based on a
 * PropertyDescriptor.
 *
 * @author Graham Williamson
 */
class PropertyDescriptorPropertyInformation implements PropertyInformation {

  static class ReadMethodAdapter implements ReadMethod {

    private final Method method;


    public ReadMethodAdapter(Method method) {
      makeAccessible(method);
      this.method = method;
    }


    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ReadMethodAdapter)) {
        return false;
      }
      ReadMethodAdapter that = (ReadMethodAdapter) o;
      return Objects.equals(method, that.method);
    }


    @Override
    public Type getGenericReturnType() {
      return method.getGenericReturnType();
    }


    @Override
    public String getName() {
      return method.getName();
    }


    @Override
    public int hashCode() {
      return Objects.hashCode(method);
    }


    @Override
    public Object invoke(Object target) throws IllegalAccessException, InvocationTargetException {
      return method.invoke(target);
    }


    @Override
    public String toString() {
      return method.toString();
    }

  }



  static class WriteMethodAdapter implements WriteMethod {

    private final Method method;


    public WriteMethodAdapter(Method method) {
      makeAccessible(method);
      this.method = method;
    }


    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof WriteMethodAdapter)) {
        return false;
      }
      WriteMethodAdapter that = (WriteMethodAdapter) o;
      return Objects.equals(method, that.method);
    }


    @Override
    public Type getGenericValueType() {
      return method.getGenericParameterTypes()[0];
    }


    @Override
    public String getName() {
      return method.getName();
    }


    @Override
    public int hashCode() {
      return Objects.hashCode(method);
    }


    @Override
    public Object invoke(Object target, Object value) throws InvocationTargetException, IllegalAccessException {
      method.invoke(target, value);
      return target;
    }


    @Override
    public String toString() {
      return method.toString();
    }

  }


  static boolean inferNullable(Method readMethod, Method writeMethod) {
    List<Annotation> names = new ArrayList<>();
    if (readMethod != null) {
      // Look at annotations on the return value
      for (Annotation a : readMethod.getAnnotations()) {
        names.add(a);
      }
    }
    if (writeMethod != null) {
      // Look at annotations on the first parameter.
      for (Annotation a : writeMethod.getParameterAnnotations()[0]) {
        names.add(a);
      }
    }

    // Check all the annotations
    for (Annotation a : names) {
      String simpleName = a.annotationType().getSimpleName();
      if (simpleName.equalsIgnoreCase("NotNull") || simpleName.equalsIgnoreCase("NonNull")) {
        return false;
      }
    }

    // default to nullable
    return true;
  }


  private static void makeAccessible(Method method) {
    if (method != null && !method.isAccessible()) {
      ReflectionAccessor.getInstance().makeAccessible(method);
    }
  }


  private final boolean isNullable;

  /** The name of the property. */
  private final String name;

  /** The underlying/wrapped PropertyDescriptor. */
  private final PropertyDescriptor propertyDescriptor;

  private WriteMethod writeMethodOverride;


  /**
   * Construct a new Property Descriptor Property Information based on the specified Property Descriptor.
   *
   * @param propertyDescriptor The PropertyDescriptor this object will wrap.
   */
  PropertyDescriptorPropertyInformation(PropertyDescriptor propertyDescriptor) {
    this.name = propertyDescriptor.getName();
    this.propertyDescriptor = propertyDescriptor;
    isNullable = inferNullable(propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod());
  }


  /**
   * Get the name of the property.
   *
   * @return The name of the property.
   */
  @Override
  public String getName() {
    return name;
  }


  /**
   * Get the public read method of the property; its getter method.
   *
   * @return The public read method of the property. If the property is not publicly readable, <code>null</code> is
   *     returned.
   */
  @Override
  public ReadMethod getReadMethod() {
    Method method = propertyDescriptor.getReadMethod();
    return method != null ? new ReadMethodAdapter(method) : null;
  }


  /**
   * Get the return type of the read method (getter method) of the property.
   *
   * @return The return type of the read method. If the property does not have a read method, returns
   *     <code>null</code>
   */
  @Override
  public Type getReadMethodReturnType() {
    if (isReadable()) {
      return getReadMethod().getGenericReturnType();
    }
    return null;
  }


  /**
   * Get the public write method of the property; its setter method.
   *
   * @return The public write method of the property. If the property is not publicly writable, <code>null</code> is
   *     returned.
   */
  @Override
  public WriteMethod getWriteMethod() {
    if (writeMethodOverride != null) {
      return writeMethodOverride;
    }

    Method method = propertyDescriptor.getWriteMethod();
    return method != null ? new WriteMethodAdapter(method) : null;
  }


  public WriteMethod getWriteMethodOverride() {
    return writeMethodOverride;
  }


  /**
   * Get the parameter type of the write method (setter method) of the property.
   *
   * @return The type of the write method parameter. If the property does not have a write method, returns
   *     <code>null</code>
   *
   * @throws IllegalArgumentException If the write method takes more than one parameter, or zero parameters.
   */
  @Override
  public Type getWriteMethodParameterType() throws IllegalArgumentException {
    WriteMethod writeMethod = getWriteMethod();
    if (writeMethod != null) {
      return writeMethod.getGenericValueType();
    }
    return null;
  }


  @Override
  public boolean isNullable() {
    return isNullable;
  }


  /**
   * Is the property publicly readable?
   * <p>
   * That is, does the property have a public getter method?
   *
   * @return <code>true</code> if the property is publicly readable; <code>false</code> otherwise.
   */
  @Override
  public boolean isReadable() {
    return getReadMethod() != null;
  }


  /**
   * Is the property both publicly readable and writable?
   * <p>
   * That is, does the property have both a public getter and public setter method?
   *
   * @return <code>true</code> if the property is publicly readable and publicly writable; <code>false</code>
   *     otherwise.
   */
  @Override
  public boolean isReadableWritable() {
    return isReadable() && isWritable();
  }


  /**
   * Is the property publicly writable?
   * <p>
   * That is, does the property have a public setter method?
   *
   * @return <code>true</code> if the property is publicly writable; <code>false</code> otherwise.
   */
  @Override
  public boolean isWritable() {
    return getWriteMethod() != null;
  }


  public void setWriteMethodOverride(WriteMethod writeMethodOverride) {
    this.writeMethodOverride = writeMethodOverride;
  }


  /**
   * Get a human-readable String representation of this object.
   *
   * @return A human-readable String representation of this object.
   */
  @Override
  public String toString() {
    String str = "PropertyDescriptorPropertyInformation["
        + "name=" + name + ","
        + "isReadable=" + isReadable() + ","
        + "readMethod=" + getReadMethod() + ","
        + "isWritable=" + isWritable() + ","
        + "writeMethod=" + getWriteMethod() + ","
        + "isReadableWritable=" + isReadableWritable()
        + "]";
    return str;
  }

}
