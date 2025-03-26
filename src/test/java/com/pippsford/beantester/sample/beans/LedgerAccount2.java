package com.pippsford.beantester.sample.beans;

import java.time.Instant;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

/**
 * A row in the account table.
 */
@Data
@Builder(toBuilder = true)
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
    justification = "Lombok generated code")
public final class LedgerAccount2 {


  /** The account ID. */
  @Nonnull
  private final String accountId;

  @Nonnull
  private final String accountType;

  /** The ID of the asset being tracked. */
  @Nonnull
  private final String assetId;

  @Nonnull
  private final List<LabelledAmount> balances;

  /** Internal ID. Not exposed on interfaces. Deprecated as not used. */
  @Deprecated
  private final long defId;

  /** A ledger specific extension. */
  private final Object extension;

  /** Internal ID. Not exposed on interfaces. */
  private final long id;

  private final boolean isExternal;

  private final boolean isLiability;

  /** Time of the last proposed transaction or finalisation. */
  private final Instant lastProposalAt;

  /** The time of the last settled balance update. */
  @Nonnull
  private final Instant lastSettledAt;


  @SuppressWarnings("unchecked")
  public <X> X getExtension() {
    return (X) extension;
  }

}
