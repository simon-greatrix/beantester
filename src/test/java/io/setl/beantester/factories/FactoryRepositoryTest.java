package io.setl.beantester.factories;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Type;
import java.time.Clock;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;

class FactoryRepositoryTest {

  @Test
  void testAllFactories() {
    TestContext.close();
    TestContext testContext = TestContext.get();
    FactoryRepository repository = testContext.getFactories();
    Set<Class<?>> limited = Set.of(Boolean.class, boolean.class, Clock.class);
    for (Type cl : repository.getRegisteredClasses()) {
      ValueFactory f = repository.getFactory(cl);

      Object o1 = f.create(ValueType.PRIMARY);
      Object o2 = f.create(ValueType.SECONDARY);
      if (Void.class.equals(cl) || void.class.equals(cl)) {
        Object o3 = f.create(ValueType.RANDOM);
        assertNull(o1);
        assertNull(o2);
        assertNull(o3);
        continue;
      }

      assertNotEquals(o1, o2, "Primary and secondary values are the same for " + cl);

      boolean seenNotPrimary = false;
      boolean seenNotSecondary = false;
      for (int i = 0; i < 100; i++) {
        Object o3 = f.create(ValueType.RANDOM);
        if (!o1.equals(o3)) {
          seenNotPrimary = true;
        }
        if (!o2.equals(o3)) {
          seenNotSecondary = true;
        }
        if (seenNotPrimary && seenNotSecondary) {
          break;
        }
      }
      if (!(seenNotPrimary && seenNotSecondary)) {
        fail("Random values just repeat primary and secondary " + cl);
      }

      Set<Object> seen = new HashSet<>();
      for (int i = 0; i < 100; i++) {
        Object o3 = f.create(ValueType.RANDOM);
        seen.add(o3);
        if (seen.size() > 10) {
          break;
        }
      }
      if (seen.size() <= 10 && !limited.contains(cl)) {
        fail("Random values not different enough for " + cl);
      }
    }
  }

}
