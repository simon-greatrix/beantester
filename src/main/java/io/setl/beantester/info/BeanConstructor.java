package io.setl.beantester.info;


import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BeanConstructor extends ModelImpl<BeanConstructor> implements Specs.BeanCreator<BeanConstructor> {

  private final Constructor<?> constructor;

  private final List<String> names;


  public BeanConstructor(Class<?> beanClass, Specs.BeanConstructor spec) {
    spec.validate();
    names = Objects.requireNonNull(spec.names(), "parameterNames");
    List<Class<?>> types = Objects.requireNonNull(spec.types(), "parameterTypes");
    if (names.size() != types.size()) {
      throw new IllegalArgumentException("Names and types must be the same size");
    }
    try {
      this.constructor = beanClass.getConstructor(types.toArray(new Class[0]));
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No constructor found for " + beanClass + " with types " + types);
    }

    for (int i = 0; i < names.size(); i++) {
      property(
          new PropertyInformation(names.get(i))
              .type(types.get(i))
              .writer((a,b)->{})
              .nullable(BeanInformationFactory.parameterIsNullable(constructor, i))
      );
    }
  }


  public BeanConstructor(Class<?> beanClass, List<String> names, List<Class<?>> types) {
    this(beanClass, new Specs.BeanConstructorImpl(names, types));
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
