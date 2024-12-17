package io.setl.beantester.mirror;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer0;
import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer3;
import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer4;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction0;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction3;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction4;
import io.setl.beantester.mirror.SerializableLambdas.SerializableLambda;

/**
 * Find method or method name from a method reference. <br><br>
 *
 * <pre>
 * // final class
 * assertNotNull(Executables.findMethod(String::isEmpty));
 *
 * //throws checked exception
 * assertNotNull(Executables.findMethod(BufferedReader::readLine));
 *
 * // final method
 * assertNotNull(Executables.findMethod(Thread::isAlive));
 *
 * // to get vararg method you must specify parameters in generics
 * assertNotNull(Executables.&lt;String, Class&lt;?&gt;[]&gt; findMethod(getClass()::getDeclaredMethod));
 *
 * // Class.forName is overloaded.
 * // to get overloaded method you must specify parameters in generics
 * assertNotNull(Executables.&lt;String&gt; findMethod(Class::forName));
 *
 * // Works with inherited methods
 * assertNotNull(Executables.findMethod(this::toString));
 *
 * assertNotNull(Executables.findMethod(MyBean::zeroArgsVoidReturningMethod));
 * assertNotNull(Executables.findMethod(new MyBean()::zeroArgsVoidReturningMethod));
 *
 * assertNotNull(Executables.findMethod(new HashMap&lt;&gt;()::entrySet));
 * assertNotNull(Executables.findMethod(getClass()::getDeclaredMethods));
 *
 * assertNotNull(Executables.findExecutable((MySerializableLambda) new MyClass()::manyParams));
 *
 * private static interface MySerializableLambda extends SerializableLambda {
 * public String methodWithManyParameters(String a1, String a2, String a3, String a4, String a5, String a6, String a7);
 * }
 *
 * public static class MyClass {
 *
 * public String manyParams(String a1, String a2, String a3, String a4, String a5, String a6, String a7) {
 * return "hello world";
 * }
 * }
 * </pre>
 */
public class Executables {

  @SuppressWarnings("unchecked")
  private static <E extends Executable> E doFindMethod(SerializableLambda lambda) {
    Executable executable = runFinder(lambda, () -> {
      SerializedLambda serializedLambda = getSerializedLambda(lambda);

      String className = Utility.compactClassName(serializedLambda.getImplClass());
      Class<?> clazz = Class.forName(className);
      Class<?>[] parameters = getParameters(serializedLambda.getImplMethodSignature());
      String implMethodName = serializedLambda.getImplMethodName();
      return "<init>".equals(implMethodName)
          ? clazz.getDeclaredConstructor(parameters)
          : clazz.getDeclaredMethod(implMethodName, parameters);
    });

    return (E) executable;
  }


  private static String doFindMethodName(SerializableLambda lambda) {
    return runFinder(lambda, () -> {
      SerializedLambda serializedLambda = getSerializedLambda(lambda);
      return serializedLambda.getImplMethodName();
    });
  }


  public static <R> Constructor<R> findConstructor(SerializableFunction0<R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, R> Constructor<R> findConstructor(SerializableFunction2<T1, T2, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, T3, R> Constructor<R> findConstructor(SerializableFunction3<T1, T2, T3, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, T3, T4, R> Constructor<R> findConstructor(SerializableFunction4<T1, T2, T3, T4, R> fn) {
    return doFindMethod(fn);
  }


  public static <R> Constructor<R> findConstructor0(SerializableFunction0<R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, R> Constructor<R> findConstructor1(SerializableFunction1<T1, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, R> Constructor<R> findConstructor2(SerializableFunction2<T1, T2, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, T3, R> Constructor<R> findConstructor3(SerializableFunction3<T1, T2, T3, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, T3, T4, R> Constructor<R> findConstructor4(SerializableFunction4<T1, T2, T3, T4, R> fn) {
    return doFindMethod(fn);
  }


  public static Executable findExecutable(SerializableLambda consumer) {
    return doFindMethod(consumer);
  }


  public static <T> Method findGetter(SerializableFunction0<T> fn) {
    return doFindMethod(fn);
  }


  public static <T1, R> Method findGetter(SerializableFunction1<T1, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, R> Method findGetter(SerializableFunction2<T1, T2, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, T3, R> Method findGetter(SerializableFunction3<T1, T2, T3, R> fn) {
    return doFindMethod(fn);
  }


  public static <T1, T2, T3, T4, R> Method findGetter(SerializableFunction4<T1, T2, T3, T4, R> fn) {
    return doFindMethod(fn);
  }


  public static Method findMethod(SerializableConsumer0 consumer) {
    return doFindMethod(consumer);
  }


  public static <T1> Method findMethod(SerializableConsumer1<T1> consumer) {
    return doFindMethod(consumer);
  }


  public static <T1, T2> Method findMethod(SerializableConsumer2<T1, T2> consumer) {
    return doFindMethod(consumer);
  }


  public static <T1, T2, T3> Method findMethod(SerializableConsumer3<T1, T2, T3> consumer) {
    return doFindMethod(consumer);
  }


  public static <T1, T2, T3, T4> Method findMethod(SerializableConsumer4<T1, T2, T3, T4> consumer) {
    return doFindMethod(consumer);
  }


  public static String findMethodName(SerializableConsumer0 consumer) {
    return doFindMethodName(consumer);
  }


  public static <T1> String findMethodName(SerializableConsumer1<T1> consumer) {
    return doFindMethodName(consumer);
  }


  public static <T1, T2> String findMethodName(SerializableConsumer2<T1, T2> consumer) {
    return doFindMethodName(consumer);
  }


  public static <T1, T2, T3> String findMethodName(SerializableConsumer3<T1, T2, T3> consumer) {
    return doFindMethodName(consumer);
  }


  public static <T1, T2, T3, T4> String findMethodName(SerializableConsumer4<T1, T2, T3, T4> consumer) {
    return doFindMethodName(consumer);
  }


  public static String findName(SerializableLambda consumer) {
    return doFindMethodName(consumer);
  }


  private static Class<?>[] getParameters(String signature) throws ClassNotFoundException {
    String[] params = Utility.methodSignatureArgumentTypes(signature);

    Class<?>[] paramTypes = new Class[params.length];
    for (int i = 0; i < params.length; i++) {
      String className = params[i];
      Class<?> clazz = ClassUtils.forName(className, null);
      paramTypes[i] = clazz;
    }
    return paramTypes;
  }


  private static SerializedLambda getSerializedLambda(SerializableLambda lambda) throws ReflectiveOperationException {
    SerializedLambda serializedLambda = null;
    for (Class<?> cl = lambda.getClass(); cl != null; cl = cl.getSuperclass()) {
      try {
        Method method = cl.getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        Object replacement = method.invoke(lambda);
        if (!(replacement instanceof SerializedLambda)) {
          // custom interface implementation
          break;
        }
        serializedLambda = (SerializedLambda) replacement;
        break;
      } catch (NoSuchMethodException e) {
        // ignore
      }
    }
    if (serializedLambda == null) {
      throw new IllegalArgumentException("Cannot find SerializedLambda for " + lambda
          + " . Is it a lambda method reference or an object?");
    }
    return serializedLambda;
  }


  private static <T> T runFinder(SerializableLambda lambda, Callable<T> finder) {
    try {
      return finder.call();
    } catch (IllegalArgumentException e) {
      throw e;

    } catch (Exception e) {
      throw new IllegalArgumentException("cannot find method for " + lambda, e);
    }
  }


  private Executables() {

  }

}
