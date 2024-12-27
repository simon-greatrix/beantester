package io.setl.beantester.factories.basic;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random String objects.
 */
public class StringValueFactory extends ValueFactory {

  /**
   * Construct a new String object factory.
   */
  public StringValueFactory() {
    super(String.class, () -> "x", () -> "y", () -> Long.toString(TestContext.get().getRandom().nextLong() & Long.MAX_VALUE, Character.MAX_RADIX));
  }

}
