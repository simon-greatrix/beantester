package com.pippsford.beantester.info.specs;

import java.util.Collection;

import com.pippsford.beantester.info.Property;
import com.pippsford.beantester.info.Specs.PropertyCustomiser;

/** A specification that sets the ignored status of a collection of properties. */
public class Ignored implements PropertyCustomiser {

  private final boolean contains;

  private final Collection<String> names;


  /**
   * New instance.
   *
   * @param names property's names
   */
  public Ignored(Collection<String> names, boolean contains) {
    this.names = names;
    this.contains = contains;
  }


  @Override
  public void accept(Property property) {
    if (names.contains(property.getName()) == contains) {
      property.setIgnored(true);
    }
  }


}
