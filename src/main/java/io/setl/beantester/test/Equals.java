package io.setl.beantester.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.setl.beantester.AssertionException;
import io.setl.beantester.TestContext;
import io.setl.beantester.ValueType;
import io.setl.beantester.info.BeanHolder;

/** Test the {@code equals()} and {@code hashCode()} methods of a bean. */
public class Equals {

  private static final Object NOT_EQUAL_TO_ANYTHING = new Object() {
    @Override
    public String toString() {
      return "NOT_EQUAL_TO_ANYTHING";
    }
  };


  private final BeanHolder holder;

  private final String[] propertyNames;

  private final Map<String, Object[]> values = new HashMap<>();

  private boolean testHashCode = true;


  /**
   * New instance that tests the specified holder.
   *
   * @param holder the holder to test
   */
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


  private void doTest(boolean isSignificant, boolean didChange, Object beanBefore, Object beanAfter, String name, Object valueBefore, Object valueAfter) {
    String change = "property " + name + " from \"" + valueBefore + "\" to \"" + valueAfter + "\"";
    if (isSignificant && didChange) {
      // changed a significant property, so must not be equal now
      if (beanBefore.equals(beanAfter)) {
        throw new AssertionException(
            holder.getBeanClass() + ".equals() is not consistent with setting " + change);
      }
    } else {
      // either did not change, or was not significant, so must be equal
      if (!beanBefore.equals(beanAfter)) {
        String msg = didChange ? " returned false after changing non-significant " : " returned false after not changing ";
        throw new AssertionException(holder.getBeanClass() + ".equals() " + msg + " " + change);
      }

      if (testHashCode && beanBefore.hashCode() != beanAfter.hashCode()) {
        String msg = didChange ? " returned different value after changing non-significant " : " returned different value after not changing ";
        throw new AssertionException(holder.getBeanClass() + ".hashCode() " + msg + " " + change);
      }
    }
  }


  public void test() {
    testAllProperties();
    testRandom();
  }


  /** Test all combinations of property values. */
  private void testAllProperties() {
    // Test all combinations
    holder.reset();
    holder.setAllProperties(ValueType.RANDOM);

    // Loop over each property
    for (String name : propertyNames) {
      boolean isSignificant = holder.isSignificant(name);

      // Loop over the property values for this property
      for (Object value1 : values.get(name)) {
        BeanHolder copy = holder.copy();
        copy.setProperty(name, value1);
        Object beanBefore = copy.newBean();

        // Verify the basic equality contract
        verifyBaseEquality(beanBefore, copy.newBean());

        // Loop over the property values for this property and verify that changing it affects equality as required.
        for (Object value2 : values.get(name)) {
          BeanHolder copy2 = copy.copy();
          boolean didChange = copy2.setProperty(name, value2);
          Object beanAfter = copy2.newBean();

          doTest(isSignificant, didChange, beanBefore, beanAfter, name, value1, value2);
        }
      }
    }
  }


  public Equals testHashCode(boolean testHashCode) {
    this.testHashCode = testHashCode;
    return this;
  }


  private void testRandom() {
    for (int i = 0; i < TestContext.get().getRuns(); i++) {
      for (String propertyName : propertyNames) {
        holder.setAllProperties(ValueType.RANDOM);
        Object bean1 = holder.newBean();
        Object bean2 = holder.newBean();
        if (!bean1.equals(bean2)) {
          throw new AssertionException(holder.getBeanClass() + ".equals() is not reflexive");
        }

        Object valueBefore = holder.readActual(propertyName);

        Object valueAfter = null;
        boolean didChange = false;
        for (int j = 0; !didChange && j < 5; j++) {
          valueAfter = holder.createValue(ValueType.RANDOM, propertyName);
          didChange = holder.setProperty(propertyName, valueAfter);
        }

        bean2 = holder.newBean();
        boolean isSignificant = holder.isSignificant(propertyName);
        doTest(isSignificant, didChange, bean1, bean2, propertyName, valueBefore, valueAfter);
      }
    }
  }


  @SuppressWarnings({"EqualsWithItself", "ExpressionComparedToItself", "ConstantValue"})
  @SuppressFBWarnings("EC_NULL_ARG")
  private void verifyBaseEquality(Object beanBefore, Object otherBean) {
    // A bean is never equal to null
    if (beanBefore.equals(null)) {
      throw new AssertionException(holder.getBeanClass() + ".equals(null) should return false");
    }

    // A bean is never equal to an incompatible class
    if (beanBefore.equals(NOT_EQUAL_TO_ANYTHING)) {
      throw new AssertionException(holder.getBeanClass() + ".equals(<Incompatible Class>) should return false");
    }

    // A bean is always equal to itself
    if (!beanBefore.equals(beanBefore)) {
      throw new AssertionException(holder.getBeanClass() + ".equals() is not identity reflexive");
    }

    // A bean is always equal to a bean with the same property values
    if (!beanBefore.equals(otherBean)) {
      System.getLogger(Equals.class.getName()).log(System.Logger.Level.INFO, "bean : " + beanBefore);
      System.getLogger(Equals.class.getName()).log(System.Logger.Level.INFO, "other: " + otherBean);
      throw new AssertionException(holder.getBeanClass() + ".equals() is not reflexive");
    }

    if (testHashCode) {
      // hashCode() must return the same value for the same object
      if (beanBefore.hashCode() != beanBefore.hashCode()) {
        throw new AssertionException(holder.getBeanClass() + ".hashCode() is not consistent");
      }

      // hashCode() must return the same value for an equal object
      if (beanBefore.hashCode() != otherBean.hashCode()) {
        throw new AssertionException(holder.getBeanClass() + ".hashCode() is not consistent with equals()");
      }
    }
  }

}
