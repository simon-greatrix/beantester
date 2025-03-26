package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Double objects.
 */
public class DoubleValueFactory extends ValueFactory {

  /**
   * Construct a new Double object factory. The primary value is 1.0 and the secondary value is 1.25. Random values are generated from a Gaussian distribution.
   */
  public DoubleValueFactory() {
    super(
        Double.class,
        () -> 1.0,
        () -> 1.25,
        () -> TestContext.get().getRandom().nextGaussian());
  }

}
