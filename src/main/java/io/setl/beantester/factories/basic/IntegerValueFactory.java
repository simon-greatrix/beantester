package io.setl.beantester.factories.basic;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Integer objects.
 */
public class IntegerValueFactory extends ValueFactory {

  /**
   * Construct a new Integer object factory.
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
