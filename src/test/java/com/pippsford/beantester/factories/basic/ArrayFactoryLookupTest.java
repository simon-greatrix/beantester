package com.pippsford.beantester.factories.basic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.ValueType;
import org.junit.jupiter.api.Test;

class ArrayFactoryLookupTest {

  @Test
  void miss() {
    TestContext.get();
    assertTrue(new ArrayFactoryLookup().getFactory(String.class).isEmpty());
  }


  @Test
  void testInts() {
    TestContext.get().setRepeatable(ArrayFactoryLookup.class.getName().hashCode());
    ArrayFactoryLookup lookup = new ArrayFactoryLookup();
    ValueFactory factory = lookup.getFactory(int[].class).orElseThrow();
    int[][] expected = {
        {387651640, 1826186642, -500859917},
        {1375929931, 982670920},
        {-823834242, -1712390060, -1896509185, 2047680016, -1055000283, 1771922821},
        {665313894, -451733610, -930848222, -1355263404},
        {}
    };
    for (int i = 0; i < 5; i++) {
      int[] array = (int[]) factory.create(ValueType.RANDOM);
      assertArrayEquals(expected[i],array);
    }

    assertArrayEquals(new int[] { 1 }, (int[]) factory.create(ValueType.PRIMARY));
    assertArrayEquals(new int[] { 2 }, (int[]) factory.create(ValueType.SECONDARY));
  }

}
