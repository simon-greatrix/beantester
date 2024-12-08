/*-
 * ​​​
 * meanmirror
 * ⁣⁣⁣
 * Copyright (C) 2020 the original author or authors.
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

package org.meanbean.mirror;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class SerializableLambdas {

  @FunctionalInterface
  public interface SerializableConsumer0 extends SerializableLambda {

    void exec() throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableConsumer1<T1> extends SerializableLambda {

    void exec(T1 t1) throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableConsumer2<T1, T2> extends SerializableLambda {

    void exec(T1 t1, T2 t2) throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableConsumer3<T1, T2, T3> extends SerializableLambda {

    void exec(T1 t1, T2 t2, T3 t3) throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableConsumer4<T1, T2, T3, T4> extends SerializableLambda {

    void exec(T1 t1, T2 t2, T3 t3, T4 t4) throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableFunction0<R> extends SerializableLambda {

    R exec() throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableFunction1<T1, R> extends SerializableLambda {

    R exec(T1 t1) throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableFunction2<T1, T2, R> extends SerializableLambda {

    R exec(T1 t1, T2 t2) throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableFunction3<T1, T2, T3, R> extends SerializableLambda {

    R exec(T1 t1, T2 t2, T3 t3) throws Throwable;

  }



  @FunctionalInterface
  public interface SerializableFunction4<T1, T2, T3, T4, R> extends SerializableLambda {

    R exec(T1 t1, T2 t2, T3 t3, T3 t4) throws Throwable;

  }



  public interface SerializableLambda extends Serializable {

  }


  /**
   * Create a lambda from a method reference.
   *
   * @param lambdaType The type of the lambda
   * @param clazz      The class containing the method
   * @param methodName The name of the method
   * @param <X>        The type of the lambda
   *
   * @return The lambda
   */
  public <X extends SerializableLambda> X createLambda(Class<X> lambdaType, Class<?> clazz, String methodName) {
    try {
      return createLambda(lambdaType, clazz.getMethod(methodName));
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No method \"" + methodName + "\" found in class " + clazz.getName(), e);
    }
  }


  /**
   * Create a lambda from a method reference.
   *
   * @param lambdaType The type of the lambda
   * @param method     The method
   * @param <X>        The type of the lambda
   *
   * @return The lambda
   */
  public <X extends SerializableLambda> X createLambda(Class<X> lambdaType, Method method) {
    try {
      MethodHandle handle = MethodHandles.lookup().unreflect(method);
      return createLambda(lambdaType, handle);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Unable to access specified method: " + method, e);
    }
  }


  /**
   * Create a lambda from a method handle.
   *
   * @param lambdaType The type of the lambda
   * @param handle     the method handle
   * @param <X>        The type of the lambda
   *
   * @return The lambda
   */
  public <X extends SerializableLambda> X createLambda(Class<X> lambdaType, MethodHandle handle) {
    return MethodHandleProxies.asInterfaceInstance(lambdaType, handle);
  }

}
