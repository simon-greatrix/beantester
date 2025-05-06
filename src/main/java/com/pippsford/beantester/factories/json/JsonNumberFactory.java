package com.pippsford.beantester.factories.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.pippsford.beantester.factories.ConvertingFactory;
import com.pippsford.beantester.factories.basic.NumberValueFactory;
import jakarta.json.Json;
import jakarta.json.JsonNumber;

/** Factory for JSON numbers. */
public class JsonNumberFactory extends ConvertingFactory {

  /** New instance. */
  public JsonNumberFactory() {
    super(
        JsonNumber.class,
        new NumberValueFactory(List.of(BigInteger.class, BigDecimal.class, Integer.class, Long.class, Double.class), 1, 2),
        o -> Json.createValue((Number) o)
    );
  }

}
