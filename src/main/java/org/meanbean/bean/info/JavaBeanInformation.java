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

import static java.lang.Character.toLowerCase;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.meanbean.bean.info.PropertyDescriptorPropertyInformation.WriteMethodAdapter;
import org.meanbean.util.ValidationHelper;

/**
 * Concrete BeanInformation that gathers and contains information about a JavaBean by using java.beans.BeanInfo.
 *
 * @author Graham Williamson
 */
class JavaBeanInformation implements BeanInformation {

  /**
   * Find all candidate write methods for the specified method descriptors.
   *
   * @param methodDescriptors The method descriptors to search for candidate write methods.
   *
   * @return A list of candidate write methods.
   */
  private static List<Method> findCandidateWriteMethods(MethodDescriptor... methodDescriptors) {
    List<Method> matches = new ArrayList<>();
    for (MethodDescriptor methodDescriptor : methodDescriptors) {
      Method method = methodDescriptor.getMethod();
      if (isCandidateWriteMethod(method)) {
        matches.add(method);
      }
    }

    return matches;
  }


  /**
   * Find the potential setter methods in the bean that do not return void and are therefore missed by regular Bean Introspection.
   *
   * @param beanInfo the bean info
   *
   * @return the additional setter methods
   */
  private static Map<String, Method> findFluentWriteMethods(BeanInfo beanInfo) {
    List<Method> methods = findCandidateWriteMethods(beanInfo.getMethodDescriptors());
    Map<String, Method> fluentMethods = new HashMap<>();
    for (Method method : methods) {
      String propertyName = method.getName().substring(3);
      propertyName = toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
      fluentMethods.put(propertyName, method);
    }
    return fluentMethods;
  }


  /**
   * Determine if the specified method is a candidate write method. The Java Bean design pattern requires that a setter returns void, but it is common for
   * the setter to return "this" to allow method chaining or the old value of the property. This method tests if the candidate is called
   * "set[Something]" and has a non-void return type.
   *
   * @param method the method to test
   *
   * @return true if the method is a candidate write method
   */
  private static boolean isCandidateWriteMethod(Method method) {
    String methodName = method.getName();
    return methodName.length() > 3
        && methodName.startsWith("set")
        && Character.isUpperCase(methodName.charAt(3))
        && Modifier.isPublic(method.getModifiers())
        && !void.class.isAssignableFrom(method.getReturnType())
        && method.getParameterCount() == 1;
  }

  /** The type of object this object contains information about. */
  private final Class<?> beanClass;

  /** The mechanism used to acquire information about the type. */
  private final BeanInfo beanInfo;

  /** Information about each property of the type, keyed by property name. */
  private final Map<String, PropertyInformation> properties = new ConcurrentHashMap<>();


  /**
   * Construct a new JavaBean Information object for the specified type.
   *
   * @param beanClass The type of the JavaBean object to gather information about.
   *
   * @throws IllegalArgumentException If the beanClass is deemed illegal. For example, if it is null.
   * @throws BeanInformationException If a problem occurred when gathering information about the specified type. This may be because the
   *                                  specified type is not a valid JavaBean.
   */
  JavaBeanInformation(Class<?> beanClass) throws IllegalArgumentException, BeanInformationException {
    ValidationHelper.ensureExists("beanClass", "gather JavaBean information", beanClass);
    this.beanClass = beanClass;
    try {
      beanInfo = Introspector.getBeanInfo(beanClass);
    } catch (IntrospectionException e) {
      throw new BeanInformationException("Failed to acquire information about beanClass [" + beanClass + "].", e);
    }
    initialize();
  }


  /**
   * Get the type of bean this object contains information about.
   *
   * @return The type of bean this object contains information about.
   */
  @Override
  public Class<?> getBeanClass() {
    return beanClass;
  }


  /**
   * Get information about all properties of the bean.
   *
   * @return A Collection of all properties of the bean.
   */
  @Override
  public Collection<PropertyInformation> getProperties() {
    return properties.values();
  }


  /**
   * Get the names of all properties of the bean.
   *
   * @return A Collection of names of all properties of the bean.
   */
  @Override
  public Collection<String> getPropertyNames() {
    return properties.keySet();
  }


  /**
   * Initialize this object ready for public use. This involves acquiring information about each property of the type.
   */
  private void initialize() {
    Map<String, Method> fluentWriteMethods = findFluentWriteMethods(beanInfo);
    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
      if ("class".equals(propertyDescriptor.getName())) {
        continue;
      }
      PropertyDescriptorPropertyInformation propertyInformation = new PropertyDescriptorPropertyInformation(propertyDescriptor);
      Method fluentWriteMethod = fluentWriteMethods.get(propertyDescriptor.getName());
      if (!propertyInformation.isWritable() && fluentWriteMethod != null) {
        propertyInformation.setWriteMethodOverride(new WriteMethodAdapter(fluentWriteMethod));
      }

      properties.put(propertyInformation.getName(), propertyInformation);
    }
  }

}
