package io.setl.beantester.info;


import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import io.setl.beantester.TestContext;
import io.setl.beantester.info.Specs.BeanCreator;
import io.setl.beantester.info.Specs.BuilderMethods;
import io.setl.beantester.info.Specs.CreatorPropertyCustomiser;
import io.setl.beantester.info.Specs.NewProperty;
import io.setl.beantester.info.Specs.PropertyCustomiser;
import io.setl.beantester.info.Specs.RemoveProperty;
import io.setl.beantester.info.Specs.Spec;
import io.setl.beantester.mirror.SerializableLambdas;
import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction0;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;

/**
 * Defines an object that creates BeanInformation objects.
 */
class BeanInformationFactory {

  /**
   * Find all the specs of a given type.
   *
   * @param type  the type spec
   * @param specs the specs to search
   *
   * @return a list of specs of the given type
   */
  static <S extends Specs.Spec> Optional<S> firstSpec(Class<S> type, Spec... specs) {
    if (specs == null) {
      return Optional.empty();
    }
    for (Spec s : specs) {
      if (type.isInstance(s)) {
        return Optional.of(type.cast(s));
      }
    }
    return Optional.empty();
  }


  /**
   * Does a method name have a given prefix?
   *
   * @param name   the method name
   * @param prefix the prefix
   *
   * @return true if the method name has the prefix
   */
  private static boolean hasPrefix(String name, String prefix) {
    return name.startsWith(prefix) && name.length() > prefix.length() && Character.isUpperCase(name.charAt(prefix.length()));
  }


  /**
   * Does an annotation indicate that a parameter is nullable?
   *
   * @param a the annotation
   *
   * @return true if the annotation indicates that the parameter is nullable
   */
  private static boolean isNullable(Annotation a) {
    String simpleName = a.annotationType().getSimpleName();
    return simpleName.equalsIgnoreCase("NotNull") || simpleName.equalsIgnoreCase("NonNull");
  }


  /**
   * Ascertain whether a parameter is nullable.
   *
   * @param executable the method or constructor
   * @param index      the parameter's index
   *
   * @return true if no not-null nor non-null annotations are present
   */
  static boolean parameterIsNullable(Executable executable, int index) {
    if (executable.getParameterTypes()[index].isPrimitive()) {
      return false;
    }
    Annotation[][] annotations = executable.getParameterAnnotations();
    for (Annotation a : annotations[index]) {
      if (isNullable(a)) {
        return false;
      }
    }
    return true;
  }


  /**
   * Ascertain whether a method's return value is nullable.
   *
   * @param method the method
   *
   * @return true if no not-null nor non-null annotations are present
   */
  static boolean returnValueIsNullable(Method method) {
    if (method.getReturnType().isPrimitive()) {
      return false;
    }
    for (Annotation a : method.getAnnotations()) {
      if (isNullable(a)) {
        return false;
      }
    }
    return true;
  }


  /**
   * Find all the specs of a given type.
   *
   * @param type  the type spec
   * @param specs the specs to search
   *
   * @return a list of specs of the given type
   */
  static <S extends Specs.Spec> List<S> specs(Class<S> type, Spec... specs) {
    if (specs == null) {
      return List.of();
    }
    return specs(type, List.of(specs));
  }


  /**
   * Find all the specs of a given type.
   *
   * @param type  the type spec
   * @param specs the specs to search
   *
   * @return a list of specs of the given type
   */
  static <S extends Specs.Spec> List<S> specs(Class<S> type, Collection<? extends Spec> specs) {
    if (specs == null) {
      return List.of();
    }
    List<S> list = new ArrayList<>();
    for (Spec s : specs) {
      if (type.isInstance(s)) {
        list.add(type.cast(s));
      }
    }
    return list;
  }


  /**
   * Remove a prefix from a name. It is assumed the name actually has the prefix.
   *
   * @param name   the name
   * @param prefix the prefix
   *
   * @return the name with the prefix removed
   */
  private static String stripPrefix(String name, String prefix) {
    return Character.toLowerCase(name.charAt(prefix.length())) + name.substring(prefix.length() + 1);
  }


  /** Properties on the bean. */
  private final TreeMap<String, PropertyInformation> beanProperties = new TreeMap<>();

  /** The bean's class. */
  private Class<?> beanClass;

  /** Creator for the bean. */
  private BeanCreator creator;

  /** Specs used in creating the bean and property information. */
  private Spec[] specs;


  private void applyPropertyCustomisers(Collection<? extends Spec> specs, Model<?> model) {
    // remove props
    for (RemoveProperty spec : specs(RemoveProperty.class, specs)) {
      model.removeProperty(spec.get());
    }

    // customise props
    for (PropertyCustomiser spec : specs(PropertyCustomiser.class, specs)) {
      for (PropertyInformation info : model.properties()) {
        try {
          spec.exec(info);
        } catch (Throwable e) {
          throw new IllegalStateException("Failed to customise property: " + info.name(), e);
        }
      }
    }

    // add props
    for (NewProperty spec : specs(NewProperty.class, specs)) {
      model.property(spec.get());
    }
  }


  /**
   * Create a BeanInformation object.
   *
   * @param testContext the test context
   * @param beanClass   the class of the bean
   * @param specs       the specs to use
   *
   * @return the BeanInformation object
   */
  BeanInformation create(TestContext testContext, Class<?> beanClass, Specs.Spec... specs) {
    this.beanClass = beanClass;
    this.specs = specs;
    beanProperties.clear();

    findCreator();
    for (CreatorPropertyCustomiser spec : specs(CreatorPropertyCustomiser.class, specs)) {
      applyPropertyCustomisers(spec.get(), creator);
    }

    findBeanProperties();
    BeanInformation information = new BeanInformation(testContext, beanClass)
        .beanCreator(creator)
        .properties(beanProperties.values());
    applyPropertyCustomisers(List.of(specs), information);

    return information;
  }


  /** Find the bean's properties. This only finds properties that have getters and setters, not those are in the builder or the constructor. */
  private void findBeanProperties() {
    Method[] methods = beanClass.getMethods();
    for (Method method : methods) {
      if (
          Modifier.isStatic(method.getModifiers())
              || Modifier.isAbstract(method.getModifiers())
              || !Modifier.isPublic(method.getModifiers())
              || method.getDeclaringClass().equals(Object.class)
      ) {
        continue;
      }

      // A method with no parameters and a return value is a getter of some kind.
      if (method.getParameterCount() == 0 && !method.getReturnType().equals(void.class)) {
        findGetter(method);
      }

      // A method with a single parameter may be a setter of some kind.
      if (method.getParameterCount() == 1) {
        findSetter(method);
      }
    }
  }


  private void findCreator() {
    // Does the bean have a specific creator?
    Optional<BeanCreator> optCreator = firstSpec(BeanCreator.class, specs);
    if (optCreator.isPresent()) {
      creator = optCreator.get();
      return;
    }

    // Does the bean have a constructor specifier?
    Optional<Specs.BeanConstructor> optConstructor = firstSpec(Specs.BeanConstructor.class, specs);
    if (optConstructor.isPresent()) {
      creator = new BeanConstructor(beanClass, optConstructor.get());
      return;
    }

    // Does the bean have a builder specifier?
    Optional<Specs.BuilderMethods> optBuilder = firstSpec(Specs.BuilderMethods.class, specs);
    if (optBuilder.isPresent()) {
      creator = new BeanBuilder(optBuilder.get());
      return;
    }

    // Look for paired "builder" and "build" methods
    optBuilder = findDefaultBuilder();
    if (optBuilder.isPresent()) {
      creator = new BeanBuilder(optBuilder.get());
      return;
    }

    // Look for a usable constructor
    optConstructor = findDefaultConstructor();
    if (optConstructor.isPresent()) {
      creator = new BeanConstructor(beanClass, optConstructor.get());
      return;
    }

    throw new IllegalStateException("Unable to construct beans of class: " + beanClass);
  }


  private Optional<BuilderMethods> findDefaultBuilder() {
    // There should be a static "builder" method on the bean class.
    Method builderMethod;
    try {
      builderMethod = beanClass.getMethod("builder");
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
    // Must be static with zero parameters
    if (!Modifier.isStatic(builderMethod.getModifiers()) || builderMethod.getParameterCount() != 0) {
      return Optional.empty();
    }

    // Now we have the builder class, try to find the build method
    Class<?> builderClass = builderMethod.getReturnType();
    Method buildMethod;
    try {
      buildMethod = builderClass.getMethod("build");
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
    // Must not be static, and must have zero parameters
    if (
        Modifier.isStatic(buildMethod.getModifiers())
            || buildMethod.getParameterCount() != 0
            || Modifier.isAbstract(buildMethod.getModifiers())
            || !beanClass.isAssignableFrom(buildMethod.getReturnType())
    ) {
      return Optional.empty();
    }

    // We have both methods, so return them
    return Optional.of(new BuilderMethods() {
      @Override
      @SuppressWarnings("unchecked")
      public SerializableFunction1<Object, Object> build() {
        return SerializableLambdas.createLambda(SerializableFunction1.class, buildMethod);
      }


      @Override
      @SuppressWarnings("unchecked")
      public SerializableFunction0<Object> builder() {
        return SerializableLambdas.createLambda(SerializableFunction0.class, builderMethod);
      }
    });
  }


  private Optional<Specs.BeanConstructor> findDefaultConstructor() {
    return Specs.beanConstructorIfPossible(beanClass);
  }


  /** Check if a method is a getter. */
  @SuppressWarnings("unchecked")
  private void findGetter(Method method) {
    // regular getter
    String methodName = method.getName();
    String propertyName;
    if (hasPrefix(methodName, "get")) {
      propertyName = stripPrefix(methodName, "get");
    } else if (
        hasPrefix(methodName, "is")
            && (
            method.getReturnType().equals(boolean.class)
                || method.getReturnType().equals(Boolean.class)
        )
    ) {
      propertyName = stripPrefix(methodName, "is");
    } else {
      propertyName = methodName;
    }

    PropertyInformation property = new PropertyInformation(propertyName)
        .reader(SerializableLambdas.createLambda(SerializableFunction1.class, method))
        .nullable(returnValueIsNullable(method));
    PropertyInformation.merge(beanProperties, property);
  }


  /** Consider if a method is a setter. */
  @SuppressWarnings("unchecked")
  private void findSetter(Method method) {
    String methodName = method.getName();
    // A setter can return the old value of a field, or the bean itself for chaining.
    Class<?> returnType = method.getReturnType();
    Class<?> valueType = method.getParameterTypes()[0];
    if (!(
        returnType.equals(void.class)
            || returnType.equals(valueType)
            || returnType.equals(beanClass)
    )) {
      // not a setter
      return;
    }

    String propertyName;
    if (hasPrefix(methodName, "set")) {
      propertyName = stripPrefix(methodName, "set");
    } else {
      propertyName = methodName;
    }

    PropertyInformation property = new PropertyInformation(propertyName)
        .nullable(parameterIsNullable(method, 0));

    if (method.getReturnType().equals(void.class)) {
      property.writer(SerializableLambdas.createLambda(SerializableConsumer2.class, method));
    } else {
      property.writer(SerializableLambdas.createLambda(SerializableFunction2.class, method));
    }

    PropertyInformation.merge(beanProperties, property);
  }


  /**
   * Find the writable properties of a class.
   *
   * @param beanClass the class to find the writable properties of
   *
   * @return a collection of writable properties
   */
  Collection<PropertyInformation> findWritableProperties(Class<?> beanClass) {
    this.beanClass = beanClass;
    this.specs = new Spec[0];
    beanProperties.clear();
    findBeanProperties();
    beanProperties.values().removeIf(p -> !p.writable());
    return beanProperties.values();
  }

}
