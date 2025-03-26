package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Character objects.
 */
public class CharacterValueFactory extends ValueFactory {


  /**
   * Construct a new Character object factory. The primary value is 'A' and the secondary value is 'B'.
   */
  public CharacterValueFactory() {
    super(
        Character.class,
        () -> 'A',
        () -> 'B',
        () -> (char) TestContext.get().getRandom().nextInt(Character.MAX_VALUE)
    );
  }

}
