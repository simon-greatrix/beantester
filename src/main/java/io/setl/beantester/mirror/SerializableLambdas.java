package io.setl.beantester.mirror;

import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Objects;

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
  public static <X extends SerializableLambda> X createLambda(Class<X> lambdaType, Class<?> clazz, String methodName) {
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
  public static <X extends SerializableLambda> X createLambda(Class<X> lambdaType, Method method) {
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
  public static <X extends SerializableLambda> X createLambda(Class<X> lambdaType, MethodHandle handle) {
    MethodType lambdaMethodType = null;
    for (Method m : lambdaType.getMethods()) {
      if (m.getName().equals("exec")) {
        lambdaMethodType = MethodType.methodType(m.getReturnType(), m.getParameterTypes());
        break;
      }
    }
    Objects.requireNonNull(lambdaMethodType, "No exec method found in lambda type");

    try {
      CallSite site = LambdaMetafactory.altMetafactory(
          MethodHandles.lookup(),
          "exec",
          MethodType.methodType(lambdaType),
          lambdaMethodType,
          handle,
          handle.type(),
          LambdaMetafactory.FLAG_SERIALIZABLE
      );

      @SuppressWarnings("unchecked") X lambda = (X) site.getTarget().invoke();
      return lambda;
    } catch (Error error) {
      throw error;
    } catch (Throwable exception) {
      throw new AssertionError("Internal error", exception);
    }
  }

}
