package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Double objects.
 */
public class DoubleValueFactory extends ValueFactory {

  /**
   * Construct a new Double object factory.
   */
  public DoubleValueFactory() {
    super(() -> 1.0, ()->1.25, ()-> TestContext.get().getRandom().nextGaussian());
  }

}
