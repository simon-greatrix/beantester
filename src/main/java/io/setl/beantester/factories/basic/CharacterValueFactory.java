package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Character objects.
 *
 * @author Graham Williamson
 */
public final class CharacterValueFactory extends RandomValueFactoryBase<Character> {


  /**
   * Construct a new Character object factory.
   *
   * @param random A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If the specified random is deemed illegal. For example, if it is null.
   */
  public CharacterValueFactory(RandomGenerator random) throws IllegalArgumentException {
    super(random);
  }


  @Override
  protected Character createPrimary() {
    return 'A';
  }


  /**
   * Create a new Character object.
   *
   * @return A new Character object.
   */
  @Override
  protected Character createRandom() {
    return (char) getRandom().nextInt(Character.MAX_VALUE);
  }


  @Override
  protected Character createSecondary() {
    return 'B';
  }

}
