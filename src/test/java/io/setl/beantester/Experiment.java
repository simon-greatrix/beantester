package io.setl.beantester;

import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.ApproverAccount;
import io.setl.beantester.example.ApproverLink;
import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.example.ApproverTransfer;
import io.setl.beantester.example.BuildableBean;
import io.setl.beantester.example.PetRecord;
import io.setl.beantester.info.Specs;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableLambda;

public class Experiment {

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


  public int[] doThing(Map<String, Set<Number>> j) {
    return new int[]{23};
  }


  public <T1, T2, R> SerializableFunction2<T1, T2, R> foo(SerializableFunction2<T1, T2, R> f) {
    return f;
  }


  public SerializableFunction2<?, ?, ?> foo2(SerializableFunction2<?, ?, ?> f) {
    return f;
  }


  @Test
  void test() throws Throwable {
    SerializableFunction2<?, ?, ?> f = foo(Experiment::doThing);
    SerializedLambda lambda = getSerializedLambda(f);
    System.out.println(lambda.getImplMethodName());
    MethodType type = MethodType.fromMethodDescriptorString(lambda.getImplMethodSignature(), getClass().getClassLoader());
    System.out.println(type);
  }


  @Test
  void test1() throws Throwable {
    BeanVerifier.verify(ApproverManifest.class, Specs.notNull("tags"));
    BeanVerifier.verify(ApproverAccount.class);
    BeanVerifier.verify(ApproverLink.class, Specs.notNull("tags"));
    BeanVerifier.verify(ApproverTransfer.class, Specs.notNull("tags"));
    BeanVerifier.verify(BuildableBean.class, Specs.notNull("tags"));
    BeanVerifier.verify(PetRecord.class);
  }

}
