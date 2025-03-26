package com.pippsford.beantester.info.specs;

import com.pippsford.beantester.info.Property;
import com.pippsford.beantester.info.Specs.PropertyCustomiser;

/** A specification that sets a property's type. */
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
    if (property.getName().equals(name)) {
      property.setType(type);
    }
  }

}
