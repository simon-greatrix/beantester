package io.setl.beantester.info.specs;

import static io.setl.beantester.info.specs.Utility.findParameters;
import static io.setl.beantester.info.specs.Utility.isNameTypeList;
import static io.setl.beantester.info.specs.Utility.isTypeList;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.setl.beantester.info.Specs.BeanConstructor;
import io.setl.beantester.info.Specs.BeanMaker;
import io.setl.beantester.info.Specs.BuilderMethods;
import io.setl.beantester.mirror.SerializableLambdas;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction0;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;

/** Factory for creating bean makers. */
public class BeanMakerFactory {

  /**
   * Create a bean maker referencing a method in the specified class. The method must be static, have a unique name and have been compiled with the '-parameter'
   * option so the parameter names are available.
   *
   * @param factoryClass the class that contains the factory methods
   * @param factoryName  the name of the factory method in the factory class
   * @param nameAndType  pairs of names and types (or just types if the names are available in the byte code)
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
    return new BeanMakerImpl(factoryClass, factoryName, parameters.getNames(), parameters.getTypes());
  }


  /**
   * Create a builder from a named method in the bean class and the corresponding build method.
   *
   * @param beanClass       the class of the bean
   * @param builderSupplier the name of the static method that returns a builder
   * @param buildFunction   the name of the method that builds the bean
   *
   * @return the builder methods specification as the only item in a list
   */
  public static BuilderMethods specBuilder(Class<?> beanClass, String builderSupplier, String buildFunction) {
    // Identify and validate the builder supplier
    Method method = validateBuilderSupplier(beanClass, builderSupplier);

    @SuppressWarnings("unchecked")
    SerializableFunction0<Object> builderSupplierImpl =
        SerializableLambdas.createLambda(SerializableFunction0.class, method);

    method = validateBuildFunction(method.getReturnType(), beanClass, buildFunction);

    @SuppressWarnings("unchecked")
    SerializableFunction1<Object, Object> buildFunctionImpl =
        SerializableLambdas.createLambda(SerializableFunction1.class, method);

    return new BeanBuilderImpl(builderSupplierImpl, buildFunctionImpl);

  }


  private static Method validateBuildFunction(Class<?> builderType, Class<?> beanClass, String buildFunction) {
    Method method;
    try {
      method = builderType.getMethod(buildFunction);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Class " + beanClass + " : Builder class " + builderType + " does not have a public method named " + buildFunction);
    }

    // must be a public instance method that has a concrete implementation
    int modifiers = method.getModifiers();
    if (
        Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers) || !Modifier.isPublic(modifiers)
    ) {
      throw new IllegalArgumentException("Method " + buildFunction + " must be a concrete public method");
    }

    // Must return a subclass of the bean class.
    if (!beanClass.isAssignableFrom(method.getReturnType())) {
      throw new IllegalArgumentException("Method " + buildFunction + " must return a sub-class of " + beanClass.getName() + " not " + method.getReturnType());
    }

    return method;
  }


  private static Method validateBuilderSupplier(Class<?> clazz, String name) {
    Method method;
    try {
      method = clazz.getMethod(name);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Class " + clazz + " does not have a public method named " + name);
    }

    // Method must be a static public method that does not return void.
    int modifiers = method.getModifiers();
    if (
        !(Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
    ) {
      throw new IllegalArgumentException("Class " + clazz + " : A builder supplying method " + name + " must be a static public method");
    }

    // Now identify the build function
    Class<?> builderType = method.getReturnType();
    if (builderType.equals(Void.TYPE)) {
      throw new IllegalArgumentException("Class " + clazz + " : A builder supplying method " + name + " must not return void");
    }

    return method;
  }

}
