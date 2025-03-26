package com.pippsford.beantester.factories.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.WildcardType;
import java.util.Optional;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.sample.beans.BankAccount;
import lombok.Data;
import org.junit.jupiter.api.Test;

class BeanFactoryLookupTest {

  @Test
  void onlyWorksForClasses() {
    WildcardType wildcardType = TestContext.get().create(WildcardType.class);
    assertFalse(new BeanFactoryLookup().getFactory(wildcardType).isPresent());
  }

  @Test
  void createsFactory() {
    BeanFactoryLookup lookup = new BeanFactoryLookup();
    Optional<ValueFactory> factoryOptional = lookup.getFactory(BankAccount.class);
    assertTrue(factoryOptional.isPresent());
    ValueFactory factory1 = factoryOptional.get();

    factoryOptional = lookup.getFactory(BankAccount.class);
    ValueFactory factory2 = factoryOptional.orElseThrow();

    assertSame(factory2, factory1);
  }

  static class BadBean {
    private String text;

    public String getText() {
      return text;
    }

    public void setText(String text) {
      if( ! text.equals("good")) {
        throw new IllegalArgumentException("Bad text");
      }
      this.text = text;
    }
  }

  @Test
  void badBean() {
    BeanFactoryLookup lookup = new BeanFactoryLookup();
    Optional<ValueFactory> factoryOptional = lookup.getFactory(BadBean.class);
    assertTrue(factoryOptional.isEmpty());
  }
}
