package io.setl.beantester.mirror;

import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A collection of functional interfaces that are serializable and can be used to create lambdas from method references.
 */
public class SerializableLambdas {

  /** A method that takes no arguments and does not return a value. */
  @FunctionalInterface
  public interface SerializableConsumer0 extends SerializableLambda {

    void exec() throws Throwable;

  }



  /** A method that takes one argument and does not return a value. */
  @FunctionalInterface
  public interface SerializableConsumer1<T1> extends SerializableLambda {

    void exec(T1 t1) throws Throwable;

  }



  /** A method that takes two arguments and does not return a value. */
  @FunctionalInterface
  public interface SerializableConsumer2<T1, T2> extends SerializableLambda {

    void exec(T1 t1, T2 t2) throws Throwable;

  }



  /** A method that takes three arguments and does not return a value. */
  @FunctionalInterface
  public interface SerializableConsumer3<T1, T2, T3> extends SerializableLambda {

    void exec(T1 t1, T2 t2, T3 t3) throws Throwable;

  }



  /** A method that takes four arguments and does not return a value. */
  @FunctionalInterface
  public interface SerializableConsumer4<T1, T2, T3, T4> extends SerializableLambda {

    void exec(T1 t1, T2 t2, T3 t3, T4 t4) throws Throwable;

  }



  /** A function that takes no arguments and returns a value (i.e. it is a supplier). */
  @FunctionalInterface
  public interface SerializableFunction0<R> extends SerializableLambda {

    R exec() throws Throwable;

  }



  /** A function that takes one argument and returns a value. */
  @FunctionalInterface
  public interface SerializableFunction1<T1, R> extends SerializableLambda {

    R exec(T1 t1) throws Throwable;

  }



  /** A function that takes two arguments and returns a value. */
  @FunctionalInterface
  public interface SerializableFunction2<T1, T2, R> extends SerializableLambda {

    R exec(T1 t1, T2 t2) throws Throwable;

  }



  /** A function that takes three arguments and returns a value. */
  @FunctionalInterface
  public interface SerializableFunction3<T1, T2, T3, R> extends SerializableLambda {

    R exec(T1 t1, T2 t2, T3 t3) throws Throwable;

  }



  /** A function that takes four arguments and returns a value. */
  @FunctionalInterface
  public interface SerializableFunction4<T1, T2, T3, T4, R> extends SerializableLambda {

    R exec(T1 t1, T2 t2, T3 t3, T3 t4) throws Throwable;

  }



  /** A marker interface for serializable lambdas. */
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

    MethodType handleType = handle.type().wrap().changeReturnType(lambdaMethodType.returnType());

    try {
      CallSite site = LambdaMetafactory.altMetafactory(
          // caller – Represents a lookup context with the accessibility privileges of the caller.
          // Specifically, the lookup context must have full privilege access.
          // When used with invokedynamic, this is stacked automatically by the VM.
          MethodHandles.lookup(),

          // interfaceMethodName – The name of the method to implement.
          // When used with invokedynamic, this is provided by the NameAndType of the InvokeDynamic structure and is stacked automatically by the VM.
          "exec",

          // factoryType – The expected signature of the CallSite. The parameter types represent the types of capture variables; the return type is the
          // interface to implement. When used with invokedynamic, this is provided by the NameAndType of the InvokeDynamic structure and is stacked
          // automatically by the VM.
          MethodType.methodType(lambdaType),

          // interfaceMethodType – Signature and return type of method to be implemented by the function object
          lambdaMethodType,

          // implementation – A direct method handle describing the implementation method which should be called (with suitable adaptation of argument types
          // and return types, and with captured arguments prepended to the invocation arguments) at invocation time.
          handle,

          // dynamicMethodType – The signature and return type that should be enforced dynamically at invocation time.
          // In simple use cases this is the same as interfaceMethodType.
          handleType,

          // flags
          LambdaMetafactory.FLAG_SERIALIZABLE
      );

      @SuppressWarnings("unchecked") X lambda = (X) site.getTarget().invoke();
      return lambda;
    } catch (Error error) {
      throw error;
    } catch (Throwable exception) {
      throw new AssertionError("Internal error converting \"" + handle + "\" to \"" + lambdaType + "\"", exception);
    }
  }

}
