package com.pippsford.beantester.sample.beans;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Singular;

/** The proposed outcome of a transfer. */
@Data
@Builder
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"}, justification = "Lombok generated code")
public final class ApproverTransfer {

  /** The amount this transaction applies to the account. */
  @Nonnull
  private final BigDecimal amount;

  /** The asset ID. */
  @Nonnull
  @NotEmpty
  private final String assetId;

  /** The transfer's correlation ID. */
  private final String correlationId;

  /** The 'from' account. */
  private final BankAccount from;

  /** The index of this transfer in the manifest. */
  private final int index;

  /** Links that constitute this transfer. */
  @Singular(ignoreNullCollections = true)
  @Nullable
  private final List<BankUpdate> links;

  /** The 'to' account. */
  private final BankAccount to;

  /** The type of the transfer. */
  @Nonnull
  @NotEmpty
  private final String type;

  /** Tags assigned to this transfer. */
  @Default
  private Set<String> tags = new HashSet<>();


  /**
   * Does this transfer have a "from" account?.
   *
   * @return true if a "from" account is present
   */
  public boolean hasFrom() {
    return from != null;
  }


  /**
   * Does this transfer have a "to" account?.
   *
   * @return true if a "to" account is present
   */
  public boolean hasTo() {
    return to != null;
  }

}
