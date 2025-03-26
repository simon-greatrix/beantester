package com.pippsford.beantester.sample.beans;

import jakarta.annotation.Nonnull;

/**
 * A mechanism for identifying balance types.
 */
public interface TypeLabel {

  /**
   * Unique label for the type.
   *
   * @return the label
   */
  @Nonnull
  String getLabel();

}
