package com.pippsford.beantester.factories.protobuf;

import java.util.random.RandomGenerator;

import com.google.protobuf.ByteString;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/** Factory for Protobuf ByteString objects. The ByteString is assumed to be a binary value. */
public class ByteStringValueFactory extends ValueFactory {

  private static final ByteString PRIMARY = ByteString.copyFromUtf8("X");

  private static final ByteString SECONDARY = ByteString.copyFromUtf8("Y");


  private static ByteString random() {
    RandomGenerator random = TestContext.get().getRandom();
    int length = random.nextInt(8) + 4;
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return ByteString.copyFrom(bytes);
  }


  /**
   * New instance.
   */
  public ByteStringValueFactory() {
    super(
        ByteString.class,
        () -> PRIMARY,
        () -> SECONDARY,
        ByteStringValueFactory::random
    );
  }

}
