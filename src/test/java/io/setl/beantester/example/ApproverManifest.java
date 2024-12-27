package io.setl.beantester.example;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;

/** The manifest of a set of transfers. */
@Data
@Builder
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"}, justification = "Lombok generated code")
public final class ApproverManifest {


  /** Correlation ID assigned by the client, if any. */
  private final String correlationId;

  /** Request ID assigned by the scheduler. */
  private final String requestId;

  /** Swarm ID assigned by the scheduler. */
  @Nonnull
  private final String swarmId;

  /** Transfers associated with this manifest. */
  @Nonnull
  private final List<ApproverTransfer> transfers;

  /** Tags assigned to this manifest. */
  @Default
  private Set<String> tags = new HashSet<>();

}
