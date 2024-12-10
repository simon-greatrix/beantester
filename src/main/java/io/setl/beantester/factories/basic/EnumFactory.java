package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

/**
 * Concrete Factory that creates random Enum constants of the specified Enum type.
 *
 * @author Graham Williamson
 */
public class EnumFactory extends RandomFactoryBase<Enum<?>> {

  /** Enum constants of the specified Enum type. */
  private final Enum<?>[] enumConstants;


  /**
   * Construct a new Factory.
   *
   * @param enumClass            The type of Enum to create Enum constants of.
   * @param randomValueGenerator A random value generator used by the Factory to generate random values.
   *
   * @throws IllegalArgumentException If any of the required parameters are deemed illegal. For example, if either is null.
   */
  public EnumFactory(Class<?> enumClass, RandomGenerator randomValueGenerator) throws IllegalArgumentException {
    super(randomValueGenerator);
    if (!enumClass.isEnum()) {
      throw new IllegalArgumentException("Cannot create EnumFactory for non-Enum class.");
    }
    this.enumConstants = (Enum<?>[]) enumClass.getEnumConstants();
  }


  /**
   * Create an Enum constant of the specified Enum type.
   *
   * @return An Enum constant of the specified Enum type.
   */
  @Override
  public Enum<?> create() {
    // Get enum constant from ordinal
    return enumConstants[getRandom().nextInt(enumConstants.length)];
  }

}
