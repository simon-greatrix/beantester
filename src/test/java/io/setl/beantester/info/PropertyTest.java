package io.setl.beantester.info;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.setl.beantester.mirror.Executables;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableLambda;

class PropertyTest {

  static class Foo {
    public String getBim() {
      return "bim";
    }
  }

  @Test
  void reader() {
    Property property = new Property("p");
    property.reader(Foo::getBim);
  }


  @Test
  void reader2() {
    Property property = new Property("p");
    SerializableFunction1<Map<String,Long>,Integer> func =(a) -> {
      return String.valueOf(a).length();
    };
    property.read(func);
    System.out.println(Executables.findGetter(func).getGenericReturnType());
    System.out.println(Executables.findGetter(func).getGenericParameterTypes()[0]);

  }


  @Test
  void writer() throws ReflectiveOperationException {
    Property property = new Property("p");
    SerializableFunction2<?,Map<String,Long>,Integer> func =(a,b) -> {
      return String.valueOf(a).length();
    };
    property.read(func);
    System.out.println(Executables.findGetter(func).getGenericReturnType());
    System.out.println(Executables.findGetter(func).getGenericParameterTypes()[0]);
    System.out.println(Executables.findGetter(func).getGenericParameterTypes()[1]);

    SerializedLambda lambda = getSerializedLambda(func);
    System.out.println(lambda);
    System.out.println(MethodType.fromMethodDescriptorString(lambda.getImplMethodSignature(), null));
  }


  @Test
  void writer2() {
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

}
