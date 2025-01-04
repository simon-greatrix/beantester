package io.setl.beantester.example;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", justification = "Lombok generated code")
@NoArgsConstructor
public class AccountBalanceUpdate {

  /** The amount. */
  @Nonnull
  private BigDecimal amount = BigDecimal.ZERO;

  /** If true, the amount is added to the current settled balances. Otherwise, the settled balance is set to the amount. */
  private boolean isDelta;

  /** The reason for the update. */
  private String reason;

}
