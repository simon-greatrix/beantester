package com.pippsford.beantester.info.specs;

import java.util.Map;

import com.pippsford.beantester.NullBehaviour;
import com.pippsford.beantester.info.Property;
import com.pippsford.beantester.info.Specs.PropertyCustomiser;

/**
 * Set the null behaviour of a property.
 */
public class OnNull implements PropertyCustomiser {

  private final Map<String, NullBehaviour> behaviours;


  /**
   * New instance with the specified behaviours.
   *
   * @param behaviours the behaviours to set
   */
  public OnNull(Map<String, NullBehaviour> behaviours) {
    this.behaviours = behaviours;
  }


  @Override
  public void accept(Property property) {
    if (behaviours.containsKey(property.getName())) {
      property.setNullBehaviour(behaviours.get(property.getName()));
    }
  }

}
