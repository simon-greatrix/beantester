package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Enum constants of the specified Enum type.
 */
public class EnumValueFactory extends RandomValueFactoryBase<Enum<?>> {

  /** Enum constants of the specified Enum type. */
  private final Enum<?>[] enumConstants;


  /**
   * Construct a new Factory.
   *
   * @param enumClass The type of Enum to create Enum constants of.
   * @param random    A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If any of the required parameters are deemed illegal. For example, if either is null.
   */
  public EnumValueFactory(Class<?> enumClass, RandomGenerator random) throws IllegalArgumentException {
    super(random);
    if (!enumClass.isEnum()) {
      throw new IllegalArgumentException("Cannot create EnumFactory for non-Enum class.");
    }
    this.enumConstants = (Enum<?>[]) enumClass.getEnumConstants();
    if (enumConstants.length < 2) {
      System.getLogger("EnumValueFactory").log(System.Logger.Level.WARNING, "Enum class has less than 2 constants. This may cause issues with some tests.");
    }
  }


  @Override
  protected Enum<?> createPrimary() {
    return enumConstants[0];
  }


  /**
   * Create an Enum constant of the specified Enum type.
   *
   * @return An Enum constant of the specified Enum type.
   */
  @Override
  protected Enum<?> createRandom() {
    // Get enum constant from ordinal
    return enumConstants[getRandom().nextInt(enumConstants.length)];
  }


  @Override
  protected Enum<?> createSecondary() {
    return enumConstants[Math.min(1, enumConstants.length - 1)];
  }

}
