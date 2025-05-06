package com.pippsford.beantester.factories.json;

import com.pippsford.beantester.TestContext;
import com.pippsford.json.CJObject;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import org.junit.jupiter.api.Test;

class JsonFactoriesTest {

  @Test
  void array() {
    TestContext tc = TestContext.get();
    tc.setRepeatable(12345);
    JsonArray value = (JsonArray) tc.create(JsonArray.class);
    System.out.println(value);
  }


  @Test
  void number() {
    TestContext tc = TestContext.get();
    tc.setRepeatable(12345);
    JsonNumber value = (JsonNumber) tc.create(JsonNumber.class);
    System.out.println(value);
  }


  @Test
  void object() {
    TestContext tc = TestContext.get();
    tc.setRepeatable(12345);
    JsonObject value = (JsonObject) tc.create(JsonObject.class);
    System.out.println(((CJObject) value).toPrettyString());
  }


  @Test
  void string() {
    TestContext tc = TestContext.get();
    tc.setRepeatable(12345);
    JsonString value = (JsonString) tc.create(JsonString.class);
    System.out.println(value);
  }

}

