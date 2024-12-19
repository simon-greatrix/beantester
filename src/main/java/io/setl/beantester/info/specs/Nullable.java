package io.setl.beantester.info.specs;

import java.util.Collection;

import io.setl.beantester.info.Property;
import io.setl.beantester.info.Specs.PropertyCustomiser;

/** A specification that sets the nullability of a collection of properties. */
public class Nullable implements PropertyCustomiser {

  private final Collection<String> names;

  private final boolean nullable;


  /**
   * New instance.
   *
   * @param names       property's names
   * @param nullable true if properties are nullable
   */
  public Nullable(Collection<String> names, boolean nullable) {
    this.names = names;
    this.nullable = nullable;
  }


  @Override
  public void accept(Property property) {
    if (names.contains(property.name())) {
      property.nullable(nullable);
    }
  }


}
