package io.setl.beantester.info.specs;

import static io.setl.beantester.info.specs.Utility.findParameters;
import static io.setl.beantester.info.specs.Utility.findParametersIfPossible;
import static io.setl.beantester.info.specs.Utility.isNameTypeList;
import static io.setl.beantester.info.specs.Utility.isTypeList;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import io.setl.beantester.info.Specs.BeanConstructor;
import io.setl.beantester.info.Specs.ResolvingSpec;
import io.setl.beantester.info.Specs.Spec;

/**
 * Factory for creating bean constructors.
 */
public class BeanConstructorFactory {

  /**
   * Specify to construct a bean using the public constructor with the lowest number of arguments for which the parameter names are known.
   *
   * @param beanClass the bean's class
   *
   * @return the specification
   */
  public static BeanConstructor beanConstructor(Class<?> beanClass) {
    return BeanConstructorFactory.beanConstructorIfPossible(beanClass)
        .orElseThrow(() -> new IllegalArgumentException("No suitable constructor found for " + beanClass));
  }


  /**
   * Specify to construct a bean using the public constructor with the specified parameter types. The parameters must alternate between names and classes.
   *
   * @param nameAndType pairs of parameter names and types
   *
   * @return the specification
   */
  public static Spec beanConstructor(Object... nameAndType) {
    Optional<BeanConstructorImpl> optImpl = isNameTypeList(nameAndType);
    if (optImpl.isPresent()) {
      return optImpl.get();
    }

    Optional<List<Class<?>>> optTypeList = isTypeList(nameAndType);
    if (optTypeList.isEmpty()) {
      throw new IllegalArgumentException("Expected pairs of names and types, or just types with names in byte code, but got: " + Arrays.toString(nameAndType));
    }

    final List<Class<?>> types = optTypeList.get();

    return (ResolvingSpec) (beanClass) -> {
      Constructor<?> c;
      try {
        c = beanClass.getConstructor(types.toArray(Class<?>[]::new));
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException("Class " + beanClass + " does not have a constructor with types " + types, e);
      }

      return List.of(findParameters(beanClass, c));
    };
  }


  /**
   * Specify to construct a bean using the public constructor with the lowest number of arguments for which the parameter names are known.
   *
   * @param beanClass the bean's class
   *
   * @return the specification
   */
  public static Optional<BeanConstructor> beanConstructorIfPossible(Class<?> beanClass) {
    Constructor<?>[] constructors = beanClass.getConstructors();

    Arrays.sort(
        constructors,
        Comparator
            .<Constructor<?>>comparingInt(Constructor::getParameterCount)
            .thenComparing(Constructor::toString)
    );

    if (constructors.length == 0) {
      return Optional.empty();
    }
    return findParametersIfPossible(beanClass, constructors[0]);
  }

}
