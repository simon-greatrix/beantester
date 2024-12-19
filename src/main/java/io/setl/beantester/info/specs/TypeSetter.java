package io.setl.beantester.info.specs;

import io.setl.beantester.info.Property;
import io.setl.beantester.info.Specs.PropertyCustomiser;

/** A specification that sets the type of a property. */
public class TypeSetter implements PropertyCustomiser {

  private final String name;

  private final Class<?> type;


  /**
   * New instance.
   *
   * @param name property's name
   * @param type property's type
   */
  public TypeSetter(String name, Class<?> type) {
    this.name = name;
    this.type = type;
  }


  @Override
  public void accept(Property property) {
    if (property.name().equals(name)) {
      property.type(type);
    }
  }

}
