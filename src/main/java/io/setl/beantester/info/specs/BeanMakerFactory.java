package io.setl.beantester.info.specs;

import static io.setl.beantester.info.specs.Utility.findParameters;
import static io.setl.beantester.info.specs.Utility.isNameTypeList;
import static io.setl.beantester.info.specs.Utility.isTypeList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.setl.beantester.info.Specs.BeanConstructor;
import io.setl.beantester.info.Specs.BeanMaker;

public class BeanMakerFactory {

  /**
   * Create a bean maker referencing a method in the specified class. The method must be static, have a unique name and have been compiled with the '-parameter'
   * option so the parameter names are available.
   */
  public static BeanMaker beanMaker(Class<?> factoryClass, String factoryName, Object... nameAndType) {
    Optional<BeanConstructorImpl> optImpl = isNameTypeList(nameAndType);
    if (optImpl.isPresent()) {
      return new BeanMakerImpl(factoryClass, factoryName, optImpl.get());
    }
    Optional<List<Class<?>>> optTypeList = isTypeList(nameAndType);
    if (optTypeList.isEmpty()) {
      throw new IllegalArgumentException("Expected pairs of names and types, or just types with names in byte code, but got: " + Arrays.toString(nameAndType));
    }

    Method method;
    try {
      method = factoryClass.getMethod(factoryName, optTypeList.get().toArray(Class[]::new));
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No method with name \"" + factoryName + "\" found in " + factoryClass + " with types " + optTypeList.get());
    }

    return new BeanMakerImpl(factoryClass, factoryName, findParameters(factoryClass, method));
  }


  /**
   * Create a bean maker referencing a method in the specified class. The method must be static, have a unique name and have been compiled with the '-parameter'
   * option so the parameter names are available.
   *
   * @param factoryClass the class that contains the factory method
   * @param factoryName  the name of the factory method in the factory class
   */
  public static BeanMaker namedBeanMaker(Class<?> factoryClass, String factoryName) {
    // Find the methods
    Method[] methods = factoryClass.getMethods();
    Method method = null;
    for (Method m : methods) {
      if (m.getName().equals(factoryName)) {
        if (method != null) {
          throw new IllegalArgumentException("Multiple methods with name \"" + factoryName + "\" found in " + factoryClass);
        }
        method = m;
      }
    }
    if (method == null) {
      throw new IllegalArgumentException("No method with name \"" + factoryName + "\" found in " + factoryClass);
    }

    BeanConstructor parameters = findParameters(factoryClass, method);
    return new BeanMakerImpl(factoryClass, factoryName, parameters.names(), parameters.types());
  }

}
