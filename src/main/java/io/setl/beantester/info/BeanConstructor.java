package io.setl.beantester.info;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.setl.beantester.AssertionException;

/** A call to a bean's constructor. */
public class BeanConstructor extends AbstractModel<BeanConstructor> implements BeanCreator<BeanConstructor> {

  private final Constructor<?> constructor;

  private final List<String> names;


  /**
   * Copy constructor.
   *
   * @param beanConstructor the bean constructor to copy
   */
  public BeanConstructor(BeanConstructor beanConstructor) {
    super(beanConstructor.properties());
    this.constructor = beanConstructor.constructor;
    this.names = beanConstructor.names;
  }


  /**
   * New instance.
   *
   * @param beanClass the class of the bean
   * @param spec      the specification for the constructor
   */
  public BeanConstructor(Class<?> beanClass, Specs.BeanConstructor spec) {
    spec.validate();
    names = List.copyOf(Objects.requireNonNull(spec.names(), "parameterNames"));
    List<Class<?>> types = Objects.requireNonNull(spec.types(), "parameterTypes");
    if (names.size() != types.size()) {
      throw new IllegalArgumentException("Names and types must be the same size");
    }

    try {
      constructor = beanClass.getConstructor(types.toArray(Class[]::new));
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No constructor found for " + beanClass + " with types " + types);
    }
    if (!constructor.trySetAccessible()) {
      throw new IllegalArgumentException("Constructor is not accessible: " + constructor);
    }

    BeanDescriptionFactory factory = new BeanDescriptionFactory(beanClass);

    for (int i = 0; i < names.size(); i++) {
      property(
          new Property(names.get(i))
              .type(types.get(i))
              .writer((a, b) -> {
                // do nothing
              })
              .notNull(factory.parameterIsNotNull(constructor, i))
      );
    }
  }


  @Override
  public Object apply(Map<String, Object> params) {
    Object[] args = new Object[names.size()];
    for (int i = 0; i < names.size(); i++) {
      args[i] = params.get(names.get(i));
    }
    try {
      return constructor.newInstance(args);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new AssertionException("Failed to create bean via constructor: " + constructor, e);
    }
  }


  public BeanConstructor copy() {
    return new BeanConstructor(this);
  }

}
