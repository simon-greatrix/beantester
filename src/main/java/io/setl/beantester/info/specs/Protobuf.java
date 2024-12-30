package io.setl.beantester.info.specs;

import java.util.Collection;
import java.util.List;

import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.Property;
import io.setl.beantester.info.Specs;
import io.setl.beantester.info.Specs.DescriptionCustomiser;
import io.setl.beantester.info.Specs.Spec;

public class Protobuf implements Specs.ResolvingSpec {


  public void customise(BeanDescription beanDescription) {
    System.out.println("Protobuf customiser : " + beanDescription.getBeanClass());
    for (Property property : beanDescription.getProperties()) {
      System.out.format("%30s %s %s %s%n", property.getName(), property.isWritable(), String.valueOf(property.isReadable()).toUpperCase(), property.getType());
    }

    System.out.println("Bean creator properties");
    for (Property property : beanDescription.getBeanCreator().getProperties()) {
      System.out.format("%20s %s %s %s %s%n", property.getName(), String.valueOf(property.isWritable()).toUpperCase(), property.isReadable(), property.getType(), property.getReadMethod());
    }

  }


  @Override
  public Collection<? extends Spec> resolve(Class<?> beanClass) {
    return List.of(
        BeanMakerFactory.specBuilder(beanClass, "newBuilder", "build"),
        Specs.beanStyle(),
        (DescriptionCustomiser) this::customise
    );
  }

}
