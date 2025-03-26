package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory that creates random Float objects.
 */
public class FloatValueFactory extends ValueFactory {

  /**
   * Construct a new Float object factory. The factory will create Float objects with a value of 1, 1.25, or a random Gaussian value.
   */
  public FloatValueFactory() {
    super(
        Float.class,
        () -> 1f,
        () -> 1.25f,
        () -> (float) TestContext.get().getRandom().nextGaussian()
    );
  }


}
