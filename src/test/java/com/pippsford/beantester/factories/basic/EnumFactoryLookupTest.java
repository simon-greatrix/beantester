package com.pippsford.beantester.factories.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.WildcardType;
import java.util.Optional;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.info.BeanDescription;
import org.junit.jupiter.api.Test;

class EnumFactoryLookupTest {

  enum DoubleEnum {ONE, TWO}



  enum EmptyEnum {}



  enum SingleEnum {SINGLE}


  @Test
  void testDoubleEnum() {
    TestContext.get();
    EnumFactoryLookup lookup = new EnumFactoryLookup();
    Optional<ValueFactory> factoryOptional = lookup.getFactory(DoubleEnum.class);
    assertTrue(factoryOptional.isPresent());
    ValueFactory factory = factoryOptional.get();
    assertEquals(DoubleEnum.class, factory.getType());
    DoubleEnum e = (DoubleEnum) factory.create(ValueType.RANDOM);
    assertTrue(e == DoubleEnum.ONE || e == DoubleEnum.TWO);
    assertEquals(DoubleEnum.ONE, factory.create(ValueType.PRIMARY));
    assertEquals(DoubleEnum.TWO, factory.create(ValueType.SECONDARY));
  }


  @Test
  void testEmptyEnum() {
    TestContext.get();
    EnumFactoryLookup lookup = new EnumFactoryLookup();
    Optional<ValueFactory> factoryOptional = lookup.getFactory(EmptyEnum.class);
    assertTrue(factoryOptional.isPresent());
    ValueFactory factory = factoryOptional.get();
    assertEquals(EmptyEnum.class, factory.getType());
    assertNull(factory.create(ValueType.RANDOM));
    assertNull(factory.create(ValueType.PRIMARY));
    assertNull(factory.create(ValueType.SECONDARY));
  }


  @Test
  void testSingleEnum() {
    TestContext.get();
    EnumFactoryLookup lookup = new EnumFactoryLookup();
    Optional<ValueFactory> factoryOptional = lookup.getFactory(SingleEnum.class);
    assertTrue(factoryOptional.isPresent());
    ValueFactory factory = factoryOptional.get();
    assertEquals(SingleEnum.class, factory.getType());
    assertEquals(SingleEnum.SINGLE, factory.create(ValueType.RANDOM));
    assertEquals(SingleEnum.SINGLE, factory.create(ValueType.PRIMARY));
    assertEquals(SingleEnum.SINGLE, factory.create(ValueType.SECONDARY));
  }

  @Test
  void missing() {
    TestContext.get();
    EnumFactoryLookup lookup = new EnumFactoryLookup();
    Optional<ValueFactory> factoryOptional = lookup.getFactory(String.class);
    assertTrue(factoryOptional.isEmpty());

    factoryOptional = lookup.getFactory(TestContext.get().create(WildcardType.class));
    assertTrue(factoryOptional.isEmpty());
  }
}
