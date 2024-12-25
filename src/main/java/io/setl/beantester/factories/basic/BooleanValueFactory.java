package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;


/**
 * Concrete Factory that creates random Boolean objects.
 */
public class BooleanValueFactory extends ValueFactory {

  /**
   * Construct a new Boolean object factory.
   */
  public BooleanValueFactory() {
    super(()->Boolean.TRUE, ()->Boolean.FALSE, () -> TestContext.get().getRandom().nextBoolean());
  }
}
