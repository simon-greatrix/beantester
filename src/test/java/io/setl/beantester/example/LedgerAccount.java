package io.setl.beantester.example;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.ToString;

/**
 * A row in the account table.
 */
@Data
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
    justification = "Lombok generated code")
public final class LedgerAccount implements BalanceData {

  /**
   * Builder for accounts.
   */
  @ToString
  public static class LedgerAccountBuilder {

    private final ArrayList<BalanceDTO> balances = new ArrayList<>();

    private String accountId;

    private String assetId;

    private long defId;

    private Object extension;

    private long id;

    private boolean isExternal = true;

    private boolean isLiability = true;

    private Instant lastProposalAt;

    private Instant lastSettledAt;

    private String type = "DEFAULT";


    /** New instance. */
    LedgerAccountBuilder() {
    }


    public LedgerAccountBuilder accountId(String accountId) {
      this.accountId = accountId;
      return this;
    }


    public LedgerAccountBuilder accountType(String type) {
      this.type = type;
      return this;
    }


    public LedgerAccountBuilder assetId(String assetId) {
      this.assetId = assetId;
      return this;
    }


    /**
     * Add a balance.
     *
     * @param balance the balance
     *
     * @return this
     */
    public LedgerAccountBuilder balance(BalanceDTO balance) {
      if (balance != null) {
        this.balances.add(balance);
      }
      return this;
    }


    public LedgerAccountBuilder balance(String label, BigDecimal balance) {
      this.balances.add(BalanceDTO.of(label, balance));
      return this;
    }


    /**
     * Add a balance.
     *
     * @param id      the balance type identifier
     * @param balance the amount
     *
     * @return this
     */
    public LedgerAccountBuilder balance(BalanceTypeIdentifier id, BigDecimal balance) {
      this.balances.add(BalanceDTO.of(id, balance));
      return this;
    }


    /**
     * Add a collection of balances.
     *
     * @param balances the balances to add
     *
     * @return this
     */
    public LedgerAccountBuilder balances(Collection<? extends BalanceDTO> balances) {
      if (balances != null) {
        balances.forEach(this::balance);
      }
      return this;
    }


    /**
     * Create the account.
     *
     * @return the account
     */
    public LedgerAccount build() {
      Objects.requireNonNull(accountId, "Cannot build AccountDTO, required attribute 'accountId' has not been set");
      Objects.requireNonNull(assetId, "Cannot build AccountDTO, required attribute 'assetId' has not been set");
      Objects.requireNonNull(type, "Cannot build AccountDTO, required attribute 'type' has not been set");
      Objects.requireNonNull(lastSettledAt, "Cannot build AccountDTO, required attribute 'lastSettledAt' has not been set");
      List<BalanceDTO> balances = switch (this.balances.size()) {
        case 0 -> List.of();
        case 1 -> List.of(this.balances.get(0));
        case 2 -> List.of(this.balances.get(0), this.balances.get(1));
        case 3 -> List.of(this.balances.get(0), this.balances.get(1), this.balances.get(2));
        default -> List.copyOf(this.balances);
      };

      return new LedgerAccount(
          this.accountId,
          this.assetId,
          balances,
          this.id,
          this.defId,
          this.lastProposalAt,
          this.lastSettledAt,
          this.isLiability,
          this.isExternal,
          this.type,
          this.extension
      );
    }


    public LedgerAccountBuilder clearBalances() {
      this.balances.clear();
      return this;
    }


    public LedgerAccountBuilder defId(long defId) {
      this.defId = defId;
      return this;
    }


    public LedgerAccountBuilder extension(Object extension) {
      this.extension = extension;
      return this;
    }


    public LedgerAccountBuilder id(long id) {
      this.id = id;
      return this;
    }


    public LedgerAccountBuilder isExternal(boolean isExternal) {
      this.isExternal = isExternal;
      return this;
    }


    public LedgerAccountBuilder isLiability(boolean isLiability) {
      this.isLiability = isLiability;
      return this;
    }


    public LedgerAccountBuilder lastProposalAt(Instant lastProposalAt) {
      this.lastProposalAt = lastProposalAt;
      return this;
    }


    public LedgerAccountBuilder lastSettledAt(Instant lastSettledAt) {
      this.lastSettledAt = lastSettledAt;
      return this;
    }

  }


  public static LedgerAccountBuilder builder() {
    return new LedgerAccountBuilder();
  }


  /** The account ID. */
  @Nonnull
  private final String accountId;

  @Nonnull
  private final String accountType;

  /** The ID of the asset being tracked. */
  @Nonnull
  private final String assetId;

  @Nonnull
  private final List<BalanceDTO> balances;

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


  private LedgerAccount(
      String accountId,
      String assetId,
      List<BalanceDTO> balances,
      long id,
      long defId,
      Instant lastProposalAt,
      Instant lastSettledAt,
      boolean isLiability,
      boolean isExternal,
      String accountType,
      Object extension
  ) {
    this.accountId = accountId;
    this.assetId = assetId;
    this.balances = balances;
    this.id = id;
    this.defId = defId;
    this.lastProposalAt = lastProposalAt;
    this.lastSettledAt = lastSettledAt;
    this.isLiability = isLiability;
    this.isExternal = isExternal;
    this.accountType = accountType;
    this.extension = extension;
  }


  @SuppressWarnings("unchecked")
  public <X> X getExtension() {
    return (X) extension;
  }


  /**
   * Create new builder from this.
   *
   * @return a new builder
   */
  public LedgerAccountBuilder toBuilder() {
    return new LedgerAccountBuilder()
        .accountId(accountId)
        .accountType(accountType)
        .assetId(assetId)
        .balances(balances)
        .id(id)
        .defId(defId)
        .lastProposalAt(lastProposalAt)
        .lastSettledAt(lastSettledAt)
        .isLiability(isLiability)
        .isExternal(isExternal)
        .extension(extension);
  }

}
