package com.pippsford.beantester.factories.json;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import jakarta.json.JsonValue;

public class JsonSingletonFactory extends ValueFactory {

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
