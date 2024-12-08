package org.meanbean.bean.info;

import org.meanbean.mirror.Executables;
import org.meanbean.mirror.SerializableLambdas.SerializableFunction0;
import org.meanbean.mirror.SerializableLambdas.SerializableFunction1;
import org.meanbean.mirror.SerializableLambdas.SerializableLambda;

/**
 * Track the API of a buildable bean. The API consists of a way of creating the builder, a way of creating the bean from the builder,
 * setters on the builder, and possibly a way of converting a bean back into a builder.
 */
class BuildableBeanDescription {

  private Class<?> beanClass;

  /** Function that creates beans from the builder. */
  private SerializableFunction0<?> beanFactory;

  /** Function that creates builders. */
  private SerializableFunction0<?> builderFactory;

  private Class<?> builderClass;

  private boolean hasToBuilder;

  BuildableBeanDescription(Class<?> beanClass) {

  }

  BuildableBeanDescription(SerializableFunction0<?> beanFactory, SerializableFunction0<?> builderFactory) {
    this.beanFactory = beanFactory;
    this.builderFactory = builderFactory;
    beanClass = Executables.findGetter(beanFactory).getReturnType();
    builderClass = Executables.findGetter(builderFactory).getReturnType();

  }

  public Class<?> beanClass() {
    return beanClass;
  }


  public String buildMethodName() {
    return buildMethodName;
  }


  public BuildableBeanDescription buildMethodName(String buildMethodName) {
    this.buildMethodName = buildMethodName;
    return this;
  }


  public Class<?> builderClass() {
    return builderClass;
  }


  public BuildableBeanDescription builderClass(Class<?> builderClass) {
    this.builderClass = builderClass;
    return this;
  }


  public String builderMethodName() {
    return builderMethodName;
  }


  public BuildableBeanDescription builderMethodName(String builderMethodName) {
    this.builderMethodName = builderMethodName;
    return this;
  }


  public BuildableBeanDescription hasToBuilder(boolean toBuilder) {
    this.hasToBuilder = toBuilder;
    return this;
  }


  public boolean hasToBuilder() {
    return hasToBuilder;
  }


  public BuildableBeanDescription setBeanClass(Class<?> beanClass) {
    this.beanClass = beanClass;
    return this;
  }

}
