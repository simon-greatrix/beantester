package io.setl.beantester.example;

import java.math.BigDecimal;
import java.time.Instant;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;


/** The proposed outcome of a transfer. */
@Data
@Builder
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NotNull_VALUE"}, justification = "Lombok generated code")
public final class LedgerTransfer {


  /** The account ID. */
  @Nonnull
  private String accountId;

  /** The account type. */
  @Nonnull
  private String accountType;

  /** The amount this transaction applies to the account. */
  @Nonnull
  private BigDecimal amount;

  /** The asset ID. */
  @Nonnull
  private String assetId;

  /** When the manifest was loaded into the database. */
  @Nonnull
  private Instant createdAt;

  /** The database record ID for the account. */
  private long dbAccountId;

  /** The database record ID for the manifest. */
  private long dbManifestId;

  private Instant finalisedAt;

  /** The database record ID. */
  private long id;

  private Boolean isAccepted;

  private boolean isFinalised;

  /** The manifest's correlation ID. */
  private String manifestCorrelationId;

  /** The transfer's request ID. */
  private String requestId;

  /** The manifest's swarm ID. */
  @Nonnull
  private String swarmId;


  /** The transfer's correlation ID. */
  private String transferCorrelationId;

}
