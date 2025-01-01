package io.setl.beantester.info;


import static io.setl.beantester.info.specs.BeanConstructorFactory.beanConstructorIfPossible;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import io.setl.beantester.info.Specs.BeanCreatorSpec;
import io.setl.beantester.info.Specs.BuilderMethods;
import io.setl.beantester.info.Specs.DescriptionCustomiser;
import io.setl.beantester.info.Specs.MethodFilterSpec;
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
class BeanDescriptionFactory {

  /**
   * Find all the specs of a given type.
   *
   * @param type  the type spec
   * @param specs the specs to search
   *
   * @return a list of specs of the given type
   */
  private static <S extends Specs.Spec> Optional<S> firstSpec(Class<S> type, Spec... specs) {
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
   * Does a method name have a given prefix? The prefix is followed by an uppercase letter.
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
   * Does an annotation indicate that a parameter is not null? The annotation must be named either "NotNull" or "NonNull" (ignoring case).
   *
   * @param a the annotation
   *
   * @return true if the annotation indicates that the parameter is not null
   */
  private static boolean isNotNull(Annotation a) {
    String simpleName = a.annotationType().getSimpleName();
    return simpleName.equalsIgnoreCase("NotNull") || simpleName.equalsIgnoreCase("NonNull");
  }


  /**
   * Does an annotation indicate that a parameter is nullable? The annotation must be named either "Nullable" and is only relevant if the default is not null.
   *
   * @param a the annotation
   *
   * @return true if the annotation indicates that the parameter is nullable
   */
  private static boolean isNullable(Annotation a) {
    String simpleName = a.annotationType().getSimpleName();
    return simpleName.equalsIgnoreCase("Nullable");
  }


  /**
   * Find all the specs of a given type.
   *
   * @param type  the type spec
   * @param specs the specs to search
   *
   * @return a list of specs of the given type
   */
  private static <S extends Specs.Spec> List<S> specs(Class<S> type, Spec... specs) {
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
  private static <S extends Specs.Spec> List<S> specs(Class<S> type, Collection<? extends Spec> specs) {
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
    String stripped = name.substring(prefix.length());

    // For names like "getURL" if th first two characters are uppercase, then don't change the case of the first character.
    if (stripped.length() >= 2 && Character.isUpperCase(stripped.charAt(0)) && Character.isUpperCase(stripped.charAt(1))) {
      return stripped;
    }

    // Flip case of first character
    return Character.toLowerCase(stripped.charAt(0)) + stripped.substring(1);
  }


  /** The bean's class. */
  private final Class<?> beanClass;

  /** Properties on the bean. */
  private final TreeMap<String, Property> beanProperties = new TreeMap<>();

  /** Are we currently analyzing a builder?. */
  private final boolean onBean;

  /** Creator for the bean. */
  private BeanCreator<?> creator;

  /** Specs used in creating the bean and property information. */
  private Spec[] specs;


  BeanDescriptionFactory(Class<?> beanClass, boolean onBean) {
    this.beanClass = beanClass;
    this.onBean = onBean;

    if (Modifier.isAbstract(beanClass.getModifiers()) && !beanClass.isInterface()) {
      throw new IllegalArgumentException("Cannot create a bean description for an abstract class: " + beanClass);
    }
  }


  private boolean acceptMethod(Method method, String prefix, boolean isSetter) {
    Class<?> paramType = isSetter ? method.getParameterTypes()[0] : null;
    for (MethodFilterSpec spec : specs(MethodFilterSpec.class, specs)) {
      if (!spec.accept(onBean, isSetter, prefix, method.getName(), method.getReturnType(), paramType)) {
        return false;
      }
    }
    return true;
  }


  private void applyDescriptionCustomisers(BeanDescription information) {
    for (DescriptionCustomiser customiser : specs(DescriptionCustomiser.class, specs)) {
      customiser.accept(information);
    }
  }


  private void applyPropertyCustomisers(Model<?> model) {
    // remove props
    for (RemoveProperty spec : specs(RemoveProperty.class, specs)) {
      ArrayList<String> toRemove = new ArrayList<>();
      for (Property property : model.getProperties()) {
        if (spec.remove(property, onBean)) {
          toRemove.add(property.getName());
        }
      }
      for (String name : toRemove) {
        model.removeProperty(name);
      }
    }

    // add props
    boolean isCreator = model instanceof BeanCreator<?>;
    for (NewProperty spec : specs(NewProperty.class, specs)) {
      Optional<Property> optProperty = spec.get(isCreator);
      optProperty.ifPresent(model::setProperty);
    }

    // customise props
    for (PropertyCustomiser spec : specs(PropertyCustomiser.class, specs)) {
      for (Property info : model.getProperties()) {
        try {
          spec.accept(info);
        } catch (Throwable e) {
          throw new IllegalStateException("Failed to customise property: " + info.getName(), e);
        }
      }
    }
  }


  /**
   * Create a BeanInformation object.
   *
   * @param specs the specs to use
   *
   * @return the BeanInformation object
   */
  BeanDescription create(Spec... specs) {
    ArrayList<Spec> specList = new ArrayList<>();
    for (Spec spec : specs) {
      if (spec instanceof Specs.ResolvingSpec) {
        LinkedList<Spec> resolveList = new LinkedList<>();
        resolveList.add(spec);
        while (!resolveList.isEmpty()) {
          Spec current = resolveList.removeFirst();
          if (current instanceof Specs.ResolvingSpec resolving) {
            resolveList.addAll(0, resolving.resolve(beanClass));
          } else {
            specList.add(current);
          }
        }
      } else {
        specList.add(spec);
      }
    }
    this.specs = specList.toArray(Spec[]::new);

    beanProperties.clear();

    findCreator();
    applyPropertyCustomisers(creator);

    findBeanProperties();
    BeanDescription information = new BeanDescription(beanClass)
        .setBeanCreator(creator)
        .setProperties(beanProperties.values());
    applyPropertyCustomisers(information);

    applyDescriptionCustomisers(information);

    // harmonise the creator and the bean properties
    for (Property beanProperty : information.getProperties()) {
      Property creatorProperty = information.getBeanCreator().getProperty(beanProperty.getName());
      if (creatorProperty == null) {
        continue;
      }

      boolean value = beanProperty.isIgnored() || creatorProperty.isIgnored();
      beanProperty.setIgnored(value);
      creatorProperty.setIgnored(value);

      value = beanProperty.isNotNull() || creatorProperty.isNotNull();
      beanProperty.setNotNull(value);
      creatorProperty.setNotNull(value);
    }
    return information;
  }


  /**
   * Check if a class or its package has a "not null" annotation.
   *
   * @return true if the class has a "not null" annotation
   */
  boolean defaultIsNotNull() {
    for (Annotation a : beanClass.getAnnotations()) {
      if (isNotNull(a)) {
        return true;
      }
    }

    Package pack = beanClass.getPackage();
    if (pack != null) {
      for (Annotation a : pack.getAnnotations()) {
        if (isNotNull(a)) {
          return true;
        }
      }
    }

    return false;
  }


  /**
   * Find the properties of a class. A property will have either a getter or a setter. Note that this will not find properties that
   * are set via the creator and do not have a getter on the bean.
   *
   * @return a collection of properties
   */
  Collection<Property> findAllProperties() {
    this.specs = new Spec[0];
    beanProperties.clear();
    findBeanProperties();
    return beanProperties.values();
  }


  /** Find the bean's properties. This only finds properties that have getters and setters, not those are in the builder or the constructor. */
  private void findBeanProperties() {
    Method[] methods = beanClass.getMethods();

    for (Method method : methods) {
      // Don't want static, public or Object methods
      if (
          Modifier.isStatic(method.getModifiers())
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
    Optional<BeanCreatorSpec> optCreator = firstSpec(BeanCreatorSpec.class, specs);
    if (optCreator.isPresent()) {
      creator = optCreator.get().getCreator();
      return;
    }

    // Does the bean have a maker specifier?
    Optional<Specs.BeanMaker> optMaker = firstSpec(Specs.BeanMaker.class, specs);
    if (optMaker.isPresent()) {
      creator = new BeanMaker(optMaker.get());
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
      creator = new BeanBuilder(beanClass, optBuilder.get());
      return;
    }

    // Interfaces need special handling
    if (beanClass.isInterface()) {
      creator = new BeanProxy(beanClass);
      return;
    }

    // Look for paired "builder" and "build" methods
    optBuilder = findDefaultBuilder();
    if (optBuilder.isPresent()) {
      creator = new BeanBuilder(beanClass, optBuilder.get());
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

    @SuppressWarnings("unchecked")
    SerializableFunction1<Object, Object> buildLambda = SerializableLambdas.createLambda(SerializableFunction1.class, buildMethod);
    @SuppressWarnings("unchecked")
    SerializableFunction0<Object> builderLambda = SerializableLambdas.createLambda(SerializableFunction0.class, builderMethod);

    // We have both methods, so return them
    return Optional.of(new BuilderMethods() {
      @Override
      public SerializableFunction1<Object, Object> getBuildFunction() {
        return buildLambda;
      }


      @Override
      public SerializableFunction0<Object> getBuilderSupplier() {
        return builderLambda;
      }
    });
  }


  private Optional<Specs.BeanConstructor> findDefaultConstructor() {
    return beanConstructorIfPossible(beanClass);
  }


  /** Check if a method is a getter. */
  @SuppressWarnings("unchecked")
  private void findGetter(Method method) {
    // regular getter
    String propertyName = findGetterName(method);
    if (propertyName == null) {
      return;
    }

    Property property = new Property(propertyName)
        .reader(SerializableLambdas.createLambda(SerializableFunction1.class, method))
        .setNotNull(returnValueIsNotNull(method));
    Property.merge(beanProperties, property);
  }


  private String findGetterName(Method method) {
    String methodName = method.getName();

    if (hasPrefix(methodName, "get") && acceptMethod(method, "get", false)) {
      return stripPrefix(methodName, "get");
    }

    if (
        hasPrefix(methodName, "is")
            && (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class))
            && acceptMethod(method, "is", false)
    ) {
      return stripPrefix(methodName, "is");
    }

    return acceptMethod(method, "", false)
        ? methodName
        : null;
  }


  /** Consider if a method is a setter. */
  @SuppressWarnings("unchecked")
  private void findSetter(Method method) {
    // A setter can return the old value of a field, or the bean itself for chaining.
    Class<?> returnType = method.getReturnType();
    Class<?> valueType = method.getParameterTypes()[0];
    if (
        !(
            returnType.equals(void.class)
                || returnType.equals(Void.class)
                || returnType.equals(valueType)
                || returnType.equals(beanClass)
        )
    ) {
      // not a setter
      return;
    }

    String propertyName = getSetterName(method);
    if (propertyName == null) {
      return;
    }

    Property property = new Property(propertyName)
        .setNotNull(parameterIsNotNull(method, 0));

    if (returnType.equals(void.class)) {
      property.setWriter(SerializableLambdas.createLambda(SerializableConsumer2.class, method));
    } else {
      property.setWriter(SerializableLambdas.createLambda(SerializableFunction2.class, method));
    }

    Property.merge(beanProperties, property);
  }


  /**
   * Find the writable properties of a class.
   *
   * @return a collection of writable properties
   */
  Collection<Property> findWritableProperties() {
    this.specs = new Spec[0];
    beanProperties.clear();
    findBeanProperties();
    beanProperties.values().removeIf(p -> !p.isWritable());
    return beanProperties.values();
  }


  private String getSetterName(Method method) {
    String methodName = method.getName();

    if (hasPrefix(methodName, "set") && acceptMethod(method, "set", true)) {
      return stripPrefix(methodName, "set");
    }

    return acceptMethod(method, "", true) ? methodName : null;
  }


  /**
   * Ascertain whether a parameter is not null.
   *
   * @param executable the method or constructor
   * @param index      the parameter's index
   *
   * @return true if a not-null or non-null is present
   */
  boolean parameterIsNotNull(Executable executable, int index) {
    // Primitive types are never null
    if (executable.getParameterTypes()[index].isPrimitive()) {
      return true;
    }

    boolean defaultIsNotNull = defaultIsNotNull();
    Annotation[][] annotations = executable.getParameterAnnotations();
    for (Annotation a : annotations[index]) {
      if (defaultIsNotNull) {
        // Must be explicitly nullable
        if (isNullable(a)) {
          return false;
        }
      } else {
        // Check if not-null
        if (isNotNull(a)) {
          return true;
        }
      }
    }

    return defaultIsNotNull;
  }


  /**
   * Ascertain whether a method's return value is "not null".
   *
   * @param method the method
   *
   * @return true if not-null or non-null annotations are present
   */
  boolean returnValueIsNotNull(Method method) {
    // primitive types are never null
    if (method.getReturnType().isPrimitive()) {
      return true;
    }

    boolean defaultIsNotNull = defaultIsNotNull();

    for (Annotation a : method.getAnnotations()) {
      if (defaultIsNotNull) {
        if (isNullable(a)) {
          return false;
        }
      } else {
        if (isNotNull(a)) {
          return true;
        }
      }
    }

    return defaultIsNotNull;
  }

}
