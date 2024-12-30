package io.setl.beantester.info.specs;

import java.util.Collection;

import io.setl.beantester.info.Property;
import io.setl.beantester.info.Specs.PropertyCustomiser;

/** A specification that sets the nullability of a collection of properties. */
public class NotNull implements PropertyCustomiser {

  private final Collection<String> names;

  private final boolean notNull;


  /**
   * New instance.
   *
   * @param names       property's names
   * @param notNull true if properties are not null
   */
  public NotNull(Collection<String> names, boolean contains, boolean notNull) {
    this.names = names;
    this.notNull = notNull;
  }


  @Override
  public void accept(Property property) {
    if (names.contains(property.getName())) {
      property.setNotNull(notNull);
    }
  }


}
