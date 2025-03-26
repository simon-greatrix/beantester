package com.pippsford.beantester.info.specs;

import java.util.Collection;

import com.pippsford.beantester.info.Property;
import com.pippsford.beantester.info.Specs.PropertyCustomiser;

/** A specification that sets the nullability of a collection of properties. */
public class NotNull implements PropertyCustomiser {

  private final boolean contains;

  private final Collection<String> names;

  private final boolean notNull;


  /**
   * New instance.
   *
   * @param names   property's names
   * @param notNull true if properties are not null
   */
  public NotNull(Collection<String> names, boolean contains, boolean notNull) {
    this.names = names;
    this.contains = contains;
    this.notNull = notNull;
  }


  @Override
  public void accept(Property property) {
    if (names.contains(property.getName()) == contains) {
      property.setNotNull(notNull);
    }
  }


}
