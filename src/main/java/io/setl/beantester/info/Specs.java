package io.setl.beantester.info;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;

public class Specs {

  /**
   * Specifies a constructor. The bean's class must have a public constructor with matching argument types.
   */
  interface BeanConstructor extends Spec {

    /** The constructor's parameter names. The parameter names must be unique and equal in number to the parameters. */
    List<String> getParameterNames();

    /** The constructor's parameter types. The bean's class must have a public constructor with these exact parameter types. */
    List<Class<?>> getParameterTypes();

  }



  /**
   * Specify to construct a bean using the specified function. The function takes a map of parameter names to values and should create the bean accordingly.
   */
  interface BeanCreator extends Spec, SerializableFunction1<Map<String, Object>, Object> {

  }


  /**
   * Specify to customise a property. During initialisation all properties will be passed to the customiser.
   */
  interface PropertyCustomiser extends Spec, SerializableConsumer1<PropertyInformation> {

  }


  interface Spec {
    // marker
  }


  /**
   * Specify to construct a bean using the public constructor with the lowest number of arguments for which the parameter names are known.
   *
   * @param beanClass the bean's class
   *
   * @return the specification
   */
  static BeanConstructor beanConstructor(Class<?> beanClass) {
    Constructor<?>[] constructors = beanClass.getConstructors();
    Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));
    for (Constructor<?> constructor : constructors) {
      boolean namesAreKnown = true;

      for (Parameter parameter : constructor.getParameters()) {
        if (!parameter.isNamePresent()) {
          namesAreKnown = false;
          break;
        }
      }

      if (namesAreKnown) {
        // found it
        List<String> names = new ArrayList<>();
        List<Class<?>> types = new ArrayList<>();

        for (Parameter parameter : constructor.getParameters()) {
          names.add(parameter.getName());
          types.add(parameter.getType());
        }

        return new BeanConstructor() {
          @Override
          public List<String> getParameterNames() {
            return Collections.unmodifiableList(names);
          }


          @Override
          public List<Class<?>> getParameterTypes() {
            return Collections.unmodifiableList(types);
          }
        };
      }
    }

    throw new IllegalStateException("No constructor with known parameter names found for: " + beanClass);
  }


  /**
   * Specify to construct a bean using the public constructor with the specified parameter types. The parameters must alternate between names and classes.
   *
   * @param nameAndType pairs of parameter names and types
   *
   * @return the specification
   */
  static BeanConstructor beanConstructor(Object... nameAndType) {
    List<String> names = new ArrayList<>();
    List<Class<?>> types = new ArrayList<>();

    int length = nameAndType.length;
    if (length % 2 != 0) {
      throw new IllegalArgumentException("Odd number of arguments: " + length);
    }
    for (int i = 0; i < length; i++) {
      Object o = nameAndType[i];
      if (i % 2 == 0) {
        if (o instanceof String str) {
          names.add(str);
        } else {
          throw new IllegalArgumentException("Expected a name (String) at index " + i + " but got: " + ((o != null) ? o.getClass().toString() : "null"));
        }
      } else {
        if (o instanceof Class<?> cls) {
          types.add(cls);
        } else {
          throw new IllegalArgumentException("Expected a type (Class<?>) at index " + i + " but got: " + ((o != null) ? o.getClass().toString() : "null"));
        }
      }
    }

    return new BeanConstructor() {
      @Override
      public List<String> getParameterNames() {
        return Collections.unmodifiableList(names);
      }


      @Override
      public List<Class<?>> getParameterTypes() {
        return Collections.unmodifiableList(types);
      }
    };

  }

}
