package com.pippsford.beantester.factories.json;

import java.util.random.RandomGenerator;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.basic.BigDecimalValueFactory;
import com.pippsford.beantester.factories.basic.BigIntegerValueFactory;
import jakarta.json.Json;
import jakarta.json.JsonNumber;

public class JsonNumberFactory extends ValueFactory {

  static Number random() {
    RandomGenerator rg = TestContext.get().getRandom();
    return switch (rg.nextInt(8)) {
      case 0 -> BigDecimalValueFactory.createRandom();
      case 1 -> BigIntegerValueFactory.createRandom();
      case 2 -> rg.nextInt(256);
      case 3 -> rg.nextExponential();
      case 4 -> rg.nextDouble();
      case 5 -> rg.nextLong();
      case 6 -> rg.nextInt(65536);
      default -> rg.nextInt();
    };
  }


  public JsonNumberFactory() {
    super(
        JsonNumber.class,
        () -> Json.createValue(1),
        () -> Json.createValue(2),
        () -> Json.createValue(random())
    );
  }

}
