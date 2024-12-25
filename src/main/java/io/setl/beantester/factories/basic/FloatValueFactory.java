package io.setl.beantester.factories.basic;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Float objects.
 */
public class FloatValueFactory extends ValueFactory {

  /**
   * Construct a new Float object factory.
   */
  public FloatValueFactory() {
    super(() -> 1f, () -> 1.25f, () -> (float) TestContext.get().getRandom().nextGaussian());
  }


}
