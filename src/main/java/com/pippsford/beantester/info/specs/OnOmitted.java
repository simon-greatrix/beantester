package com.pippsford.beantester.info.specs;

import java.util.Map;

import com.pippsford.beantester.NullBehaviour;
import com.pippsford.beantester.info.Property;
import com.pippsford.beantester.info.Specs.PropertyCustomiser;

/** Set the omitted behaviour of a property. */
public class OnOmitted implements PropertyCustomiser {

  private final Map<String, NullBehaviour> behaviours;


  /**
   * New instance with the specified behaviours.
   *
   * @param behaviours the behaviours to set
   */
  public OnOmitted(Map<String, NullBehaviour> behaviours) {
    this.behaviours = behaviours;
  }


  @Override
  public void accept(Property property) {
    if (behaviours.containsKey(property.getName())) {
      property.setOmittedBehaviour(behaviours.get(property.getName()));
    }
  }

}
