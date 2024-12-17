package io.setl.beantester.example;

import jakarta.annotation.Nonnull;

/**
 * A mechanism for identifying balance types.
 */
public interface BalanceTypeIdentifier {

  /**
   * Unique label for the type.
   *
   * @return the label
   */
  @Nonnull
  String getLabel();

}
