package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;


/**
 * Factory that creates random Boolean objects.
 */
public class BooleanValueFactory extends ValueFactory {

  /**
   * Construct a new Boolean object factory. The primary value is true and the secondary value is false. Random values could be either.
   */
  public BooleanValueFactory() {
    super(
        Boolean.class,
        () -> Boolean.TRUE,
        () -> Boolean.FALSE,
        () -> TestContext.get().getRandom().nextBoolean()
    );
  }

}
