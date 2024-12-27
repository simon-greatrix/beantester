package io.setl.beantester.factories.basic;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Long objects.
 */
public class LongValueFactory extends ValueFactory {

  /**
   * Construct a new Long object factory.
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
