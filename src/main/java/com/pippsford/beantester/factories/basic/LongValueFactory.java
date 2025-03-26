package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory that creates random Long objects.
 */
public class LongValueFactory extends ValueFactory {

  /**
   * Construct a new Long object factory. The factory will create Long objects with a value of 1, 2, or a random long.
   */
  public LongValueFactory() {
    super(
        Long.class,
        () -> 1L,
        () -> 2L,
        () -> TestContext.get().getRandom().nextLong()
    );
  }

}
