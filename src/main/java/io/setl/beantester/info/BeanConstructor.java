package io.setl.beantester.info;


import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** A call to a bean's constructor. */
public class BeanConstructor extends AbstractModel<BeanConstructor> implements Specs.BeanCreator<BeanConstructor> {

  private final Constructor<?> constructor;

  private final List<String> names;


  /**
   * New instance.
   *
   * @param beanClass the class of the bean
   * @param spec      the specification for the constructor
   */
  public BeanConstructor(Class<?> beanClass, Specs.BeanConstructor spec) {
    spec.validate();
    names = Objects.requireNonNull(spec.names(), "parameterNames");
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

    for (int i = 0; i < names.size(); i++) {
      property(
          new Property(names.get(i))
              .type(types.get(i))
              .writer((a, b) -> {
                // do nothing
              })
              .nullable(BeanDescriptionFactory.parameterIsNullable(constructor, i))
      );
    }
  }


  @Override
  public Object exec(Map<String, Object> params) throws Throwable {
    Object[] args = new Object[names.size()];
    for (int i = 0; i < names.size(); i++) {
      args[i] = params.get(names.get(i));
    }
    return constructor.newInstance(args);
  }

}
