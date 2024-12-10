package org.meanbean.bean.info;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;

import org.meanbean.mirror.Executables;
import org.meanbean.mirror.SerializableLambdas;
import org.meanbean.mirror.SerializableLambdas.SerializableFunction0;
import org.meanbean.mirror.SerializableLambdas.SerializableFunction1;

/**
 * Track the API of a buildable bean. The API consists of a way of creating the builder, a way of creating the bean from the builder,
 * setters on the builder, and possibly a way of converting a bean back into a builder.
 */
class BuildableBeanInformation implements BeanInformation {

  private static SerializableFunction1<?, ?> findToBuilder(Class<?> beanType, Class<?> builderType) {
    try {
      Method m = beanType.getMethod("toBuilder");
      if (builderType.equals(m.getReturnType()) && !Modifier.isStatic(m.getModifiers())) {
        return SerializableLambdas.createLambda(SerializableFunction1.class, m);
      }
    } catch (NoSuchMethodException e) {
      // ignore
    }
    return null;
  }


  private Class<?> beanClass;

  /** Function that creates beans from the builder. */
  private SerializableFunction1<?, ?> beanFactory;

  private Class<?> builderClass;

  /** Function that creates builders. */
  private SerializableFunction0<?> builderFactory;

  private SerializableFunction1<?, ?> toBuilder;


  BuildableBeanInformation(Class<?> beanClass) {
    this.beanClass = beanClass;

    try {
      Method m = beanClass.getMethod("builder");
      if (!Modifier.isStatic(m.getModifiers())) {
        throw new IllegalArgumentException("Method \"builder\" in class " + beanClass.getName() + " must be static and return a builder class");
      }
      this.builderFactory = SerializableLambdas.createLambda(SerializableFunction0.class, m);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No method \"builder\" found in class " + beanClass.getName(), e);
    }

    builderClass = Executables.findGetter(builderFactory).getReturnType();
    try {
      Method m = builderClass.getMethod("build");
      beanFactory = SerializableLambdas.createLambda(SerializableFunction1.class, m);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No method \"build\" found in class " + builderClass.getName(), e);
    }

    toBuilder = findToBuilder(beanClass, builderClass);
  }


  BuildableBeanInformation(SerializableFunction0<?> builderFactory, SerializableFunction1<?, ?> beanFactory) {
    this.beanFactory = beanFactory;
    this.builderFactory = builderFactory;
    beanClass = Executables.findGetter(beanFactory).getReturnType();
    builderClass = Executables.findGetter(builderFactory).getReturnType();

    Method[] beanMethods = beanClass.getMethods();
    for (Method m : beanMethods) {
      if (m.getName().equals("toBuilder") && builderClass.isAssignableFrom(m.getReturnType()) && m.getParameterCount() == 0) {
        toBuilder = SerializableLambdas.createLambda(SerializableFunction1.class, m);
        break;
      }
    }

    toBuilder = findToBuilder(beanClass, builderClass);
  }


  public Class<?> builderClass() {
    return builderClass;
  }


  @Override
  public Class<?> getBeanClass() {
    return beanClass;
  }


  @Override
  public Collection<PropertyInformation> getProperties() {
    return Collections.emptyList(); // TODO
  }


  @Override
  public Collection<String> getPropertyNames() {
    return Collections.emptyList(); // TODO
  }


  public SerializableFunction0<?> getBuilderFactory() {
    return builderFactory;
  }


  public <B> Object newBean(B builder) {
    @SuppressWarnings("unchecked")
    SerializableFunction1<B, Object> factory = (SerializableFunction1<B, Object>) beanFactory;
    try {
      return factory.exec(builder);
    } catch (Throwable e) {
      throw new AssertionError("Failed to create builder of type " + builderClass + " for " + beanClass, e);
    }
  }


  public Object newBuilder() {
    try {
      return builderFactory.exec();
    } catch (Throwable e) {
      throw new AssertionError("Failed to create builder of type " + builderClass + " for " + beanClass, e);
    }
  }


  public BuildableBeanInformation setToBuilder(SerializableFunction1<?, ?> toBuilder) {
    this.toBuilder = toBuilder;
    return this;
  }


  public SerializableFunction1<?, ?> toBuilder() {
    return toBuilder;
  }


  @Override
  public String toString() {
    return "BuildableBeanDescription{" +
        "beanClass=" + beanClass +
        ", beanFactory=" + Executables.findGetter(beanFactory) +
        ", builderClass=" + builderClass +
        ", builderFactory=" + Executables.findGetter(builderFactory) +
        ", toBuilder=" + (toBuilder == null ? "N/A" : Executables.findGetter(toBuilder).toString()) +
        '}';
  }

}
