package io.setl.beantester.factories.basic;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random Character objects.
 */
public class CharacterValueFactory extends ValueFactory {


  /**
   * Construct a new Character object factory.
   */
  public CharacterValueFactory() {
    super(
        Character.class,
        () -> 'A',
        () -> 'B',
        () -> (char) TestContext.get().getRandom().nextInt(Character.MAX_VALUE)
    );
  }

}
