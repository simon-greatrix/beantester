package io.setl.beantester.info;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.setl.beantester.AssertionException;

/** A call to a bean's factory method which accepts a list of parameters and returns the bean. */
public class BeanMaker extends AbstractModel<BeanMaker> implements BeanCreator<BeanMaker> {

  private final Method method;

  private final List<String> names;


  /**
   * Copy constructor.
   *
   * @param maker the bean maker to copy
   */
  public BeanMaker(BeanMaker maker) {
    super(maker.properties());
    this.method = maker.method;
    this.names = maker.names;
  }


  /**
   * New instance.
   *
   * @param spec the specification for the maker
   */
  public BeanMaker(Specs.BeanMaker spec) {
    spec.validate();
    names = List.copyOf(Objects.requireNonNull(spec.names(), "parameterNames"));
    List<Class<?>> types = Objects.requireNonNull(spec.types(), "parameterTypes");
    if (names.size() != types.size()) {
      throw new IllegalArgumentException("Names and types must be the same size");
    }

    try {
      method = spec.factoryClass().getMethod(spec.factoryName(), types.toArray(Class[]::new));
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No method with name \"" + spec.factoryName() + "\" found for " + spec.factoryClass() + " with types " + types);
    }
    if (!method.trySetAccessible()) {
      throw new IllegalArgumentException("Method is not accessible: " + method);
    }
    if (!Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("Method must be static: " + method);
    }

    BeanDescriptionFactory factory = new BeanDescriptionFactory(spec.factoryClass());

    for (int i = 0; i < names.size(); i++) {
      property(
          new Property(names.get(i))
              .type(types.get(i))
              .writer((a, b) -> {
                // do nothing
              })
              .notNull(factory.parameterIsNotNull(method, i))
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
      return method.invoke(null, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new AssertionException("Unable to create bean via method: " + method, e);
    }
  }


  public BeanMaker copy() {
    return new BeanMaker(this);
  }

}
