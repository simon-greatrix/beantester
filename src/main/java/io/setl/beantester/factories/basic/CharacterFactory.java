package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Character objects.
 *
 * @author Graham Williamson
 */
public final class CharacterFactory extends RandomFactoryBase<Character> {


  /**
   * Construct a new Character object factory.
   *
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified randomValueGenerator is deemed illegal. For example, if it is null.
   */
  public CharacterFactory(RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
  }


  /**
   * Create a new Character object.
   *
   * @return A new Character object.
   */
  @Override
  public Character create() {
    return (char) getRandom().nextInt(Character.MAX_VALUE);
  }

}
