package io.setl.beantester.info;

import java.util.Collection;
import java.util.Map;

import io.setl.beantester.info.Specs.BeanCreator;
import io.setl.beantester.info.Specs.BuilderMethods;

/**
 * Specification of a bean creator that uses a builder pattern.
 */
public class BeanBuilder extends AbstractModel<BeanBuilder> implements BeanCreator<BeanBuilder> {

  private final BuilderMethods builderMethods;


  /**
   * New instance.
   *
   * @param builderMethods the methods to create the builder and the bean
   */
  public BeanBuilder(
      BuilderMethods builderMethods
  ) {
    this.builderMethods = builderMethods;

    Class<?> builderClass;
    try {
      builderClass = builderMethods.builder().exec().getClass();
    } catch (Throwable e) {
      throw new IllegalStateException("Failed to create builder", e);
    }

    Collection<Property> foundProperties = new BeanDescriptionFactory().findWritableProperties(builderClass);

    for (Property property : foundProperties) {
      property(property);
    }
  }


  /**
   * Create a builder, set the properties and build the object.
   *
   * @param values the values for the builder
   *
   * @return the built bean
   *
   * @throws Throwable if something goes wrong
   */
  public Object build(Map<String, Object> values) throws Throwable {
    Object builder = builderMethods.builder().exec();
    for (Property property : properties()) {
      String name = property.name();
      if (values.containsKey(name)) {
        property.write(builder, values.get(name));
      }
    }
    return builder;
  }


  @Override
  public Object exec(Map<String, Object> values) throws Throwable {
    Object builder = build(values);
    return builderMethods.build().exec(builder);
  }

}
