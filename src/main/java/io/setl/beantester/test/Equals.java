package io.setl.beantester.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.ValueType;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.util.AssertionUtils;

public class Equals {

  private static final NotEqualToAnthing NOT_EQUAL_TO_ANYTHING = new NotEqualToAnthing();


  ;



  private static class NotEqualToAnthing {

  }



  private final BeanHolder holder;


  private final String[] propertyNames;

  private final Map<String, Object[]> values = new HashMap<>();

  private boolean testHashCode = true;


  public Equals(BeanHolder holder) {
    this.holder = holder;

    TreeSet<String> names = new TreeSet<>(holder.getPropertyNames());
    names.removeIf(name -> !holder.isTestable(name));
    propertyNames = names.toArray(String[]::new);

    for (String name : propertyNames) {
      Object[] valueArray = new Object[6];

      valueArray[0] = holder.createValue(ValueType.PRIMARY, name);
      valueArray[1] = holder.createValue(ValueType.SECONDARY, name);
      for (int i = 2; i < 6; i++) {
        valueArray[i] = holder.createValue(ValueType.RANDOM, name);
      }

      if (holder.isNullable(name)) {
        valueArray[2] = null;
      }

      values.put(name, valueArray);
    }
  }


  public void test() {
    testAllProperties();
    testRandom();
  }


  private void testAllProperties() {
    // Test all combinations
    holder.reset();
    holder.setAllProperties(ValueType.RANDOM, false);
    for (String name : propertyNames) {
      boolean isSignificant = holder.isSignificant(name);

      for (Object value1 : values.get(name)) {
        BeanHolder copy = holder.copy();
        copy.setProperty(name, value1);
        Object beanBefore = copy.bean();

        if (beanBefore.equals(null)) {
          AssertionUtils.fail(holder.getBeanClass() + ".equals(null) should return false");
        }
        if (beanBefore.equals(NOT_EQUAL_TO_ANYTHING)) {
          AssertionUtils.fail(holder.getBeanClass() + ".equals(<Other Class>) should return false");
        }

        if (!beanBefore.equals(beanBefore)) {
          AssertionUtils.fail(holder.getBeanClass() + ".equals() is not identity reflexive");
        }
        Object otherBean = copy.bean();
        if (!beanBefore.equals(otherBean)) {
          AssertionUtils.fail(holder.getBeanClass() + ".equals() is not reflexive");
        }

        if (testHashCode) {
          if (beanBefore.hashCode() != beanBefore.hashCode()) {
            AssertionUtils.fail(holder.getBeanClass() + ".hashCode() is not consistent");
          }
          if (beanBefore.hashCode() != otherBean.hashCode()) {
            AssertionUtils.fail(holder.getBeanClass() + ".hashCode() is not consistent with equals()");
          }
        }

        for (Object value2 : values.get(name)) {
          BeanHolder copy2 = copy.copy();
          boolean didChange = copy2.setProperty(name, value2);
          Object beanAfter = copy2.bean();

          if (isSignificant && didChange) {
            // changed a significant property, so must not be equal any more
            if (beanBefore.equals(beanAfter)) {
              AssertionUtils.fail(holder.getBeanClass() + ".equals() is not consistent with setProperty()");
            }
          } else {
            // either did not change, or was not significant, so must be equal
            if (!beanBefore.equals(beanAfter)) {
              String msg = didChange ? " returned false after changing non-significant property" : " returned false after not changing property";
              AssertionUtils.fail(holder.getBeanClass() + ".equals() " + msg + " " + name);
            }

            if (beanBefore.hashCode() != beanAfter.hashCode()) {
              String msg = didChange ? " returned false after changing non-significant property" : " returned false after not changing property";
              AssertionUtils.fail(holder.getBeanClass() + ".hashCode() " + msg + " " + name);
            }
          }
        }
      }
    }
  }


  public Equals testHashCode(boolean testHashCode) {
    this.testHashCode = testHashCode;
    return this;
  }


  private void testRandom() {
    RandomGenerator random = holder.testContext().getRandom();
    holder.reset();

    int paramCount = propertyNames.length;

    for (int trial = 0; trial < 5; trial++) {
      for (int i = 0; i < 3 * paramCount; i++) {
        String property = propertyNames[random.nextInt(paramCount)];
        Object value = holder.createValue(ValueType.RANDOM, property);
        holder.setProperty(property, value);
      }

      for (int i = 0; i < paramCount; i++) {
        holder.verify(propertyNames[i]);
      }
    }
  }

}
