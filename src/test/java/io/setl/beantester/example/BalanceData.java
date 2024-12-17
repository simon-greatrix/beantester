package io.setl.beantester.example;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.Nonnull;


/**
 * A collection of balances associated with an account.
 */
public interface BalanceData {

  private static boolean hasAmount(BalanceDTO dto) {
    return dto != null && dto.getAmount() != null;
  }

  /** The account ID. */
  @Nonnull
  String getAccountId();

  /** The asset ID. */
  @Nonnull
  String getAssetId();

  /**
   * Get the balance that matches the type.
   *
   * @param type the type to match
   *
   * @return the balance or zero.
   */
  default BigDecimal getBalance(BalanceTypeIdentifier type) {
    return getBalance(type.getLabel());
  }

  /**
   * Get the balance that matches the label.
   *
   * @param label the label
   *
   * @return the balance or null.
   */
  default BigDecimal getBalance(String label) {
    List<BalanceDTO> balances = getBalances();
    for (BalanceDTO dto : balances) {
      if (dto != null && dto.getLabel().equals(label) && dto.getAmount() != null) {
        return dto.getAmount();
      }
    }
    return null;
  }

  @Nonnull
  List<BalanceDTO> getBalances();

  /** The 'hot' balance, which assumes all pending transactions succeed. */
  default BigDecimal getHotBalance() {
    return getBalance("HOT");
  }

  /** The 'settled' balance, which ignores all pending transactions. Some ledger implementations may not be able to supply this. */
  default BigDecimal getSettledBalance() {
    return getBalance("SETTLED");
  }

  /**
   * Test if a specific balance type is available.
   *
   * @param type the balance type
   *
   * @return true if it is available
   */
  default boolean has(BalanceTypeIdentifier type) {
    return has(type.getLabel());
  }

  /**
   * Test if a specific balance type is available.
   *
   * @param label the label for the balance
   *
   * @return true if it is available
   */
  default boolean has(String label) {
    List<BalanceDTO> balances = getBalances();
    for (BalanceDTO dto : balances) {
      if (dto != null && dto.getLabel().equals(label) && dto.getAmount() != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Select a balance using a preference list of codes to match.
   *
   * @param labels the preference list of labels. If null or empty, returns the first balance.
   *
   * @return the matched Balance, or zero.
   */
  default BigDecimal select(String... labels) {
    return select(BigDecimal.ZERO, labels);
  }

  /**
   * Select a balance using a preference list of codes to match.
   *
   * @param dflt   the default balance to return if no match is found. For example, zero or null.
   * @param labels the preference list of labels. If null or empty, returns the first balance.
   *
   * @return the matched Balance, or the default.
   */
  default BigDecimal select(BigDecimal dflt, String... labels) {
    List<BalanceDTO> balances = getBalances();

    if (labels == null || labels.length == 0) {
      for (BalanceDTO dto : balances) {
        if (hasAmount(dto)) {
          return dto.getAmount();
        }
      }
      return dflt;
    }

    for (String l : labels) {
      for (BalanceDTO dto : balances) {
        if (hasAmount(dto) && dto.getLabel().equals(l)) {
          return dto.getAmount();
        }
      }
    }

    return dflt;
  }

  /**
   * Select a balance using a preference list of codes to match.
   *
   * @param dflt   the default balance to return if no match is found. For example, zero or null.
   * @param labels the preference list of labels. If null or empty, returns the first balance.
   *
   * @return the matched Balance, or the default.
   */
  default BigDecimal select(BigDecimal dflt, List<String> labels) {
    List<BalanceDTO> balances = getBalances();

    if (labels == null || labels.isEmpty()) {
      for (BalanceDTO dto : balances) {
        if (hasAmount(dto)) {
          return dto.getAmount();
        }
      }
      return dflt;
    }

    for (String l : labels) {
      for (BalanceDTO dto : balances) {
        if (hasAmount(dto) && dto.getLabel().equals(l)) {
          return dto.getAmount();
        }
      }
    }

    return dflt;
  }

}
