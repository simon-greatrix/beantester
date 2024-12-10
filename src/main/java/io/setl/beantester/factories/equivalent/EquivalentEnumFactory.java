package io.setl.beantester.factories.equivalent;

import org.meanbean.lang.EquivalentFactory;
import org.meanbean.util.ValidationHelper;

/**
 * Concrete Factory that always creates the same Enum constant of the specified Enum type.
 *
 * @author Graham Williamson
 */
public class EquivalentEnumFactory implements EquivalentFactory<Enum<?>> {

  /** Enum constants of the specified Enum type. */
  private final Enum<?>[] enumConstants;


  /**
   * Construct a new Factory.
   *
   * @param enumClass The type of Enum to create Enum constants of.
   *
   * @throws IllegalArgumentException If enumClass is deemed illegal. For example, if it is null.
   */
  public EquivalentEnumFactory(Class<?> enumClass) throws IllegalArgumentException {
    ValidationHelper.ensureExists("enumClass", "construct EnumFactory", enumClass);
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
    return enumConstants[0];
  }

}
