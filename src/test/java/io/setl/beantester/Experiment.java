package io.setl.beantester;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.BuildableBean;
import io.setl.beantester.example.PetRecord;
import io.setl.beantester.mirror.Executables;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;

public class Experiment {

  @Test
  void test() {
    SerializableFunction2<List<String>, Integer, String> f = (a, b) -> a.get(b).toString();
    System.out.println(Executables.findGetter(f));

  }

  @Test
  void test1() {
    BuildableBean bean = BuildableBean.builder()
        .amount(BigDecimal.TEN)
        .assetId("USD")
        .type("CASH")
        .build();

    Class<?> beanClass = bean.getClass();
    for(Constructor<?> constructor : beanClass.getDeclaredConstructors()) {
      System.out.println(constructor);
      Parameter[] parameters = constructor.getParameters();
      for(Parameter parameter : parameters) {
        System.out.println(parameter+ " " + parameter.isNamePresent());
      }
    }

    beanClass = PetRecord.class;
    for(Constructor<?> constructor : beanClass.getDeclaredConstructors()) {
      System.out.println(constructor);
      Parameter[] parameters = constructor.getParameters();
      for(Parameter parameter : parameters) {
        System.out.println(parameter+ " " + parameter.isNamePresent());
      }
    }
  }

}
