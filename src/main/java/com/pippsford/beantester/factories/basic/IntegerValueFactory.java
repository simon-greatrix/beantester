package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory that creates random Integer objects.
 */
public class IntegerValueFactory extends ValueFactory {

  /**
   * Construct a new Integer object factory. The factory will create Integer objects with a value of 1, 2, or a random integer.
   */
  public IntegerValueFactory() {
    super(
        Integer.class,
        () -> 1,
        () -> 2,
        () -> TestContext.get().getRandom().nextInt()
    );
  }

}
