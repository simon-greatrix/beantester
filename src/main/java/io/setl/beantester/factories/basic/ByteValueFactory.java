package io.setl.beantester.factories.basic;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Byte objects.
 */
public class ByteValueFactory extends ValueFactory {

  private static final Byte PRIMARY = (byte) 0;

  private static final Byte SECONDARY = (byte) 1;


  /**
   * Construct a new Byte object factory.
   */
  public ByteValueFactory() {
    super(Byte.class, () -> PRIMARY, () -> SECONDARY, () -> (byte) TestContext.get().getRandom().nextInt(256));
  }

}
