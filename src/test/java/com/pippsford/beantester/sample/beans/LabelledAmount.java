package com.pippsford.beantester.sample.beans;

import java.math.BigDecimal;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;

/**
 * A balance of a specified type.
 */
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "CT_CONSTRUCTOR_THROW"}, justification = "Lombok generated code")
public class LabelledAmount implements TypeLabel {

  /**
   * New instance.
   *
   * @param label  the label
   * @param amount the amount
   *
   * @return the new instance
   */
  public static LabelledAmount of(@Nonnull String label, BigDecimal amount) {
    if (label == null) {
      throw new IllegalArgumentException("Cannot build BalanceDTO, required attribute 'label' has not been set");
    }
    return new LabelledAmount(amount, label);
  }


  /**
   * New instance.
   *
   * @param id     the identifier
   * @param amount the amount
   *
   * @return the new instance
   */
  public static LabelledAmount of(@Nonnull TypeLabel id, BigDecimal amount) {
    if (id == null) {
      throw new IllegalArgumentException("Cannot build BalanceDTO, required attribute 'id' has not been set");
    }
    if (id.getLabel() == null) {
      throw new IllegalArgumentException("Cannot build BalanceDTO, required attribute 'label' has not been set");
    }
    return new LabelledAmount(amount, id.getLabel());
  }


  private final BigDecimal amount;

  private final String label;


  private LabelledAmount(BigDecimal amount, @Nonnull String label) {
    this.amount = amount;
    this.label = label;
  }


  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof LabelledAmount other)) {
      return false;
    }
    return Objects.equals(other.label, label) && Objects.equals(other.amount, amount);
  }


  public BigDecimal getAmount() {
    return this.amount;
  }


  @Nonnull
  public String getLabel() {
    return this.label;
  }


  @Override
  public int hashCode() {
    return Objects.hash(label, amount);
  }


  @Override
  public String toString() {
    return "BalanceDTO(amount=" + this.getAmount() + ", label=" + this.getLabel() + ")";
  }

}
