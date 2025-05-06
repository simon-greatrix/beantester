package com.pippsford.beantester.factories.basic;

import java.util.random.RandomGenerator;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

public class NumberValueFactory extends ValueFactory {

  public NumberValueFactory() {
    super(
        Number.class, false, () -> 1, () -> 2, () -> {
          RandomGenerator rg = TestContext.get().getRandom();
          return switch (rg.nextInt(8)) {
            case 0 -> BigDecimalValueFactory.createRandom();
            case 1 -> BigIntegerValueFactory.createRandom();
            case 2 -> (byte) rg.nextInt(256);
            case 3 -> rg.nextGaussian();
            case 4 -> (float) rg.nextDouble();
            case 5 -> rg.nextLong();
            case 6 -> (short) rg.nextInt(65536);
            default -> rg.nextInt();
          };
        }
    );
  }

}
