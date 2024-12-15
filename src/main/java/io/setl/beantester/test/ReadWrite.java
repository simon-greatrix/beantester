package io.setl.beantester.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.ValueType;
import io.setl.beantester.info.BeanHolder;

public class ReadWrite {

  private final BeanHolder holder;

  private final String[] propertyNames;

  private final Map<String, Object[]> values = new HashMap<>();


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


  public void test() {
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
            System.getLogger("ReadWrite").log(System.Logger.Level.INFO, "Testing " + name1 + " and " + name2 + " with values " + value1 + " and " + value2);

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
