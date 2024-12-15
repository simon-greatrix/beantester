package io.setl.beantester.info;

import java.util.Collection;
import java.util.Map;

import io.setl.beantester.info.Specs.BeanCreator;
import io.setl.beantester.info.Specs.BuilderMethods;

public class BeanBuilder extends ModelImpl<BeanBuilder> implements BeanCreator<BeanBuilder> {

  private final BuilderMethods builderMethods;


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

    Collection<PropertyInformation> foundProperties = new BeanInformationFactory().findWritableProperties(builderClass);

    for (PropertyInformation property : foundProperties) {
      property(property);
    }
  }


  @Override
  public Object exec(Map<String, Object> values) throws Throwable {
    Object builder = builderMethods.builder().exec();
    for (PropertyInformation propertyInformation : properties()) {
      String name = propertyInformation.name();
      if (values.containsKey(name)) {
        propertyInformation.write(builder, values.get(name));
      }
    }
    return builderMethods.build().exec(builder);
  }

}
