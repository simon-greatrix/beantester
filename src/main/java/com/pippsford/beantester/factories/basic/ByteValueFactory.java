package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory that creates random Byte objects.
 */
public class ByteValueFactory extends ValueFactory {

  private static final Byte PRIMARY = (byte) 0;

  private static final Byte SECONDARY = (byte) 1;


  /**
   * Construct a new Byte object factory. The primary value is 0 and the secondary value is 1.
   */
  public ByteValueFactory() {
    super(Byte.class, () -> PRIMARY, () -> SECONDARY, () -> (byte) TestContext.get().getRandom().nextInt(256));
  }

}
