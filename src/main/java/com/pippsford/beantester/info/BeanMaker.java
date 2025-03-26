package com.pippsford.beantester.info;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.pippsford.beantester.AssertionException;
import com.pippsford.beantester.info.Specs.Spec;

/** A call to a bean's factory method which accepts a list of parameters and returns the bean. */
public class BeanMaker extends AbstractCreatorModel<BeanMaker> {

  private final Method method;

  private final List<String> names;


  /**
   * Copy constructor.
   *
   * @param maker the bean maker to copy
   */
  public BeanMaker(BeanMaker maker) {
    super(maker.getProperties());
    this.method = maker.method;
    this.names = maker.names;
  }


  /**
   * New instance.
   *
   * @param spec the specification for the maker
   * @param specs     the specifications for the bean
   */
  public BeanMaker(Specs.BeanMaker spec, Spec... specs) {
    spec.validate();
    names = List.copyOf(Objects.requireNonNull(spec.getNames(), "parameterNames"));
    List<Class<?>> types = Objects.requireNonNull(spec.getTypes(), "parameterTypes");
    if (names.size() != types.size()) {
      throw new IllegalArgumentException("Names and types must be the same size");
    }

    try {
      method = spec.getFactoryClass().getMethod(spec.getFactoryName(), types.toArray(Class[]::new));
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No method with name \"" + spec.getFactoryName() + "\" found for " + spec.getFactoryClass() + " with types " + types);
    }
    if (!method.trySetAccessible()) {
      throw new IllegalArgumentException("Method is not accessible: " + method);
    }
    if (!Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("Method must be static: " + method);
    }

    BeanDescriptionFactory factory = new BeanDescriptionFactory(method.getReturnType(), specs, true);

    for (int i = 0; i < names.size(); i++) {
      setProperty(
          new Property(names.get(i))
              .setType(types.get(i))
              .setWriter((a, b) -> {
                // do nothing
              })
              .setNotNull(factory.parameterIsNotNull(method, i))
      );
    }
  }


  @Override
  public Object apply(Map<String, Object> params) {
    if (isChanged()) {
      for (String name : names) {
        Property property = getProperty(name);
        if (property == null) {
          throw new AssertionException("Class " + method.getReturnType() + " : Cannot create. Missing required property " + name);
        }
      }
      clearChanged();
    }

    Object[] args = new Object[names.size()];
    for (int i = 0; i < names.size(); i++) {
      args[i] = params.get(names.get(i));
    }
    try {
      return method.invoke(null, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new AssertionException("Unable to create bean via method: " + method, e);
    }
  }


  public BeanMaker copy() {
    return new BeanMaker(this);
  }

}
