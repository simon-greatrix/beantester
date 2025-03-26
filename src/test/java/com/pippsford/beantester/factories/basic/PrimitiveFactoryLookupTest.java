package com.pippsford.beantester.factories.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.WildcardType;

import com.pippsford.beantester.TestContext;
import org.junit.jupiter.api.Test;

class PrimitiveFactoryLookupTest {

  @Test
  void test() {
    PrimitiveFactoryLookup lookup = new PrimitiveFactoryLookup();
    assertEquals(BooleanValueFactory.class, lookup.getFactory(boolean.class).orElseThrow().getClass());
    assertEquals(ByteValueFactory.class, lookup.getFactory(byte.class).orElseThrow().getClass());
    assertEquals(ShortValueFactory.class, lookup.getFactory(short.class).orElseThrow().getClass());
    assertEquals(CharacterValueFactory.class, lookup.getFactory(char.class).orElseThrow().getClass());
    assertEquals(IntegerValueFactory.class, lookup.getFactory(int.class).orElseThrow().getClass());
    assertEquals(LongValueFactory.class, lookup.getFactory(long.class).orElseThrow().getClass());
    assertEquals(FloatValueFactory.class, lookup.getFactory(float.class).orElseThrow().getClass());
    assertEquals(DoubleValueFactory.class, lookup.getFactory(double.class).orElseThrow().getClass());

    WildcardType wildcardType = TestContext.get().create(WildcardType.class);
    assertTrue(lookup.getFactory(wildcardType).isEmpty());
  }

}
