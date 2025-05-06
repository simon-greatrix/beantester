package com.pippsford.beantester.factories.json;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import jakarta.json.JsonValue;

/** Factory for JSON Singletons: true, false, and null. */
public class JsonSingletonFactory extends ValueFactory {

  /** New instance. */
  public JsonSingletonFactory() {
    super(
        JsonValue.class,
        () -> JsonValue.FALSE,
        () -> JsonValue.TRUE,
        () -> switch (TestContext.get().getRandom().nextInt(3)) {
          case 0 -> JsonValue.TRUE;
          case 1 -> JsonValue.FALSE;
          default -> JsonValue.NULL;
        }
    );
  }

}
