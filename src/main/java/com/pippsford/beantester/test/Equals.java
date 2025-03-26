package com.pippsford.beantester.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiPredicate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.pippsford.beantester.AssertionException;
import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.info.BeanHolder;

/** Test the {@code equals()} and {@code hashCode()} methods of a bean. */
public class Equals {

  private static final Object NOT_EQUAL_TO_ANYTHING = new Object() {
    @Override
    public String toString() {
      return "NOT_EQUAL_TO_ANYTHING";
    }
  };

  private static final Map<Class<?>, BiPredicate<Object, Object>> PRIMITIVE_ARRAY_COMPARATORS = Map.of(
      boolean[].class, (a, b) -> Arrays.equals((boolean[]) a, (boolean[]) b),
      byte[].class, (a, b) -> Arrays.equals((byte[]) a, (byte[]) b),
      char[].class, (a, b) -> Arrays.equals((char[]) a, (char[]) b),
      double[].class, (a, b) -> Arrays.equals((double[]) a, (double[]) b),
      float[].class, (a, b) -> Arrays.equals((float[]) a, (float[]) b),
      int[].class, (a, b) -> Arrays.equals((int[]) a, (int[]) b),
      long[].class, (a, b) -> Arrays.equals((long[]) a, (long[]) b),
      short[].class, (a, b) -> Arrays.equals((short[]) a, (short[]) b)
  );


  /**
   * Compare two objects for equality.
   *
   * @param a an object
   * @param b an object
   *
   * @return true if {@code a} equals {@code b}
   */
  public static boolean equals(Object a, Object b) {
    // If they are the same object, they are equal
    if (a == b) {
      return true;
    }

    // if one of them is null, they are not equal
    if (a == null || b == null) {
      return false;
    }

    // Try the equals method
    if (a.equals(b)) {
      return true;
    }

    // Probably not equal, but could be two identical arrays.
    if (a instanceof Object[] a1 && b instanceof Object[] b1) {
      return Arrays.deepEquals(a1, b1);
    }

    // Could be a primitive array
    Class<?> aClass = a.getClass();
    if (aClass.equals(b.getClass())) {
      BiPredicate<Object, Object> predicate = PRIMITIVE_ARRAY_COMPARATORS.get(aClass);
      if (predicate != null) {
        return predicate.test(a, b);
      }
    }

    return false;
  }

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
