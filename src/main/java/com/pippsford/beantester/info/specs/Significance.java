package com.pippsford.beantester.info.specs;

import java.util.Collection;

import com.pippsford.beantester.info.Property;
import com.pippsford.beantester.info.Specs.PropertyCustomiser;

/** A specification that sets the significance of a collection of properties. */
public class Significance implements PropertyCustomiser {

  private final boolean contains;

  private final Collection<String> names;

  private final boolean significant;


  /**
   * New instance.
   *
   * @param names       property's names
   * @param significant true if properties are significant
   */
  public Significance(Collection<String> names, boolean contains, boolean significant) {
    this.names = names;
    this.contains = contains;
    this.significant = significant;
  }


  @Override
  public void accept(Property property) {
    if (names.contains(property.getName()) == contains) {
      property.setSignificant(significant);
    }
  }


}
