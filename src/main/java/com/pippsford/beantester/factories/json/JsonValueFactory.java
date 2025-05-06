package com.pippsford.beantester.factories.json;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.ValueType;
import jakarta.json.JsonValue;

public class JsonValueFactory extends ValueFactory {

  public JsonValueFactory(
      JsonArrayFactory arrayFactory,
      JsonObjectFactory objectFactory,
      JsonNumberFactory numberFactory,
      JsonStringFactory stringFactory,
      JsonSingletonFactory singletonFactory
  ) {
    super(
        JsonValue.class,
        false,
        () -> JsonValue.TRUE,
        () -> JsonValue.FALSE,
        () -> {
          TestContext context = TestContext.get();
          boolean isDeep = ValueFactory.getStructureDepth() >= context.getStructureDepth();
          int choice = context.getRandom().nextInt(isDeep ? 3 : 5);
          return switch (choice) {
            case 3 -> arrayFactory.create(ValueType.RANDOM);
            case 4 -> objectFactory.create(ValueType.RANDOM);
            case 1 -> numberFactory.create(ValueType.RANDOM);
            case 2 -> stringFactory.create(ValueType.RANDOM);
            default -> singletonFactory.create(ValueType.RANDOM);
          };
        }
    );
  }

}
