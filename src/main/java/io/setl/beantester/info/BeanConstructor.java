package io.setl.beantester.info;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.setl.beantester.AssertionException;
import io.setl.beantester.info.Specs.Spec;

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
    super(beanConstructor.getProperties());
    this.constructor = beanConstructor.constructor;
    this.names = beanConstructor.names;
  }


  /**
   * New instance.
   *
   * @param beanClass the class of the bean
   * @param spec      the specification for the constructor
   * @param specs     the specifications for the bean
   */
  public BeanConstructor(Class<?> beanClass, Specs.BeanConstructor spec, Spec... specs) {
    spec.validate();
    names = List.copyOf(Objects.requireNonNull(spec.getNames(), "parameterNames"));
    List<Class<?>> types = Objects.requireNonNull(spec.getTypes(), "parameterTypes");
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

    BeanDescriptionFactory factory = new BeanDescriptionFactory(beanClass, specs, true);

    for (int i = 0; i < names.size(); i++) {
      setProperty(
          new Property(names.get(i))
              .setType(types.get(i))
              .setWriter((a, b) -> {
                // do nothing
              })
              .setNotNull(factory.parameterIsNotNull(constructor, i))
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
