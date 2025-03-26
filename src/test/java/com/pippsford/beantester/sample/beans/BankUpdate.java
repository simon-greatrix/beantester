package com.pippsford.beantester.sample.beans;

import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

/**
 * An update to a bank account
 */
@Data
@Builder
public class BankUpdate {

  @Nonnull
  private final BankAccount account;

  @Nonnull
  private final AccountAction action;

  /** The index of this update. */
  private final int index;

  /** Tags assigned to this update. */
  @Default
  private Set<String> tags = new HashSet<>();

}

