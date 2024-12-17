package io.setl.beantester.example;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;


/** The manifest of a set of transfers. */
@Data
@Builder
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NotNullL_VALUE"}, justification = "Lombok generated code")
public final class LedgerManifest {


  /** Correlation ID assigned by the client, if any. */
  private String correlationId;

  /** When this transaction was loaded into the database. */
  @Nonnull
  private Instant createdAt;

  /** Time at which the manifest was finalised, if it has been. */
  private Timestamp finalisedAt;

  /** The internal ID of the manifest. */
  private long id;

  /** Was the manifest accepted, rejected, or is it still undecided?. */
  private Boolean isAccepted;

  /** Request ID assigned by the scheduler. */
  private String requestId;

  /** Swarm ID assigned by the scheduler. */
  @Nonnull
  private String swarmId;

  /** Transfers associated with this manifest. */
  @Nonnull
  private List<LedgerTransfer> transfers;


  public boolean isFinalised() {
    return finalisedAt != null;
  }

}
