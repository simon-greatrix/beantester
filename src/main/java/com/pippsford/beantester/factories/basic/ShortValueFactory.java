package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory that creates random Short objects.
 */
public class ShortValueFactory extends ValueFactory {

  /**
   * Construct a new Short object factory. The factory will create Short objects with a value of 1, 2, or a random short.
   */
  public ShortValueFactory() {
    super(
        Short.class,
        () -> (short) 1,
        () -> (short) 2,
        () -> (short) TestContext.get().getRandom().nextInt(Short.MIN_VALUE, 1 + Short.MAX_VALUE)
    );
  }

}
