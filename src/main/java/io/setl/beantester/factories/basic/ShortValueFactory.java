package io.setl.beantester.factories.basic;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Short objects.
 */
public class ShortValueFactory extends ValueFactory {

  /**
   * Construct a new Short object factory.
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
