package com.pippsford.beantester.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.random.RandomGenerator;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.info.BeanHolder;

/** Test that all the properties can be written and read correctly. */
public class ReadWrite {

  private final BeanHolder holder;

  private final String[] propertyNames;

  private final Map<String, Object[]> values = new HashMap<>();


  /**
   * New instance to test beans from the specified holder.
   *
   * @param holder source of beans
   */
  public ReadWrite(BeanHolder holder) {
    this.holder = holder;
    TreeSet<String> names = new TreeSet<>(holder.getPropertyNames());
    names.removeIf(name -> !holder.isTestable(name));
    propertyNames = names.toArray(String[]::new);

    for (String name : propertyNames) {
      if (holder.isNullable(name)) {
        values.put(name, new Object[]{
            holder.createValue(ValueType.PRIMARY, name),
            holder.createValue(ValueType.SECONDARY, name),
            null
        });
      } else {
        values.put(name, new Object[]{
            holder.createValue(ValueType.PRIMARY, name),
            holder.createValue(ValueType.SECONDARY, name)
        });
      }
    }
  }


  /** Test the setting and reading of properties. */
  public void test() {
    testToString();
    testAllCombinations();
    testRandom();
  }


  private void testAllCombinations() {
    // Test all combinations
    for (String name1 : propertyNames) {
      Object[] values1 = values.get(name1);
      for (Object value1 : values1) {

        for (String name2 : propertyNames) {
          Object[] values2 = values.get(name2);
          for (Object value2 : values2) {

            holder.reset();

            // Note that setting a property does not start the creation process, so this does not test re-setting a property.
            holder.setProperty(name1, value1);
            holder.setProperty(name2, value2);

            holder.verify(name1);
            holder.verify(name2);
          }
        }
      }
    }
  }


  private void testRandom() {
    RandomGenerator random = TestContext.get().getRandom();
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


  private void testToString() {
    Objects.requireNonNull(holder.newBean().toString());
    Optional<Object> builder = holder.createBuilder();
    builder.ifPresent(o -> Objects.requireNonNull(o.toString()));
  }

}
