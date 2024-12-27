package io.setl.beantester.example;

import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * A link in a transfer.
 */
@Data
@Builder
public class ApproverLink {

  @Nonnull
  private final ApproverAccount account;

  @Nonnull
  private final AccountAction action;

  /** The index of this link in the transfer. */
  private final int index;

  /** Tags assigned to this link. */
  @Default
  private Set<String> tags = new HashSet<>();

}

