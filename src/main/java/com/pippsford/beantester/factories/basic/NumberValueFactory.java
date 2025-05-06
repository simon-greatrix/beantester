package com.pippsford.beantester.factories.basic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/** Factory for Numbers. */
public class NumberValueFactory extends ValueFactory {

  private static final Map<Class<? extends Number>, Supplier<? extends Number>> factories = Map.of(
      BigDecimal.class, BigDecimalValueFactory::createRandom,
      BigInteger.class, BigDecimalValueFactory::createRandom,
      Byte.class, () -> (byte) TestContext.get().getRandom().nextInt(256),
      Short.class, () -> (short) TestContext.get().getRandom().nextInt(65536),
      Integer.class, () -> TestContext.get().getRandom().nextInt(),
      Long.class, () -> TestContext.get().getRandom().nextLong(),
      Double.class, () -> TestContext.get().getRandom().nextGaussian(),
      Float.class, () -> (float) TestContext.get().getRandom().nextExponential()
  );


  private static Number forClasses(List<Class<? extends Number>> classes) {
    int choice = TestContext.get().getRandom().nextInt(classes.size());
    return factories.get(classes.get(choice)).get();
  }


  /** New instance. */
  public NumberValueFactory() {
    this(List.copyOf(factories.keySet()), 1, 2);
  }


  public NumberValueFactory(List<Class<? extends Number>> classes, Number primary, Number secondary) {
    super(Number.class, () -> primary, () -> secondary, () -> forClasses(classes));
  }

}
