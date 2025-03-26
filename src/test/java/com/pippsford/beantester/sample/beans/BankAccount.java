package com.pippsford.beantester.sample.beans;


import java.util.Objects;

import jakarta.annotation.Nonnull;

/**
 * An account in a transfer.
 */
public class BankAccount {


  @Nonnull
  private final String account;


  @Nonnull
  private final String assetId;

  @Nonnull
  private final String bank;

  @Nonnull
  private final String branch;


  /**
   * New instance.
   *
   * @param account the account
   * @param branch  the bank branch
   * @param bank    the bank
   * @param assetId the asset
   */
  public BankAccount(
      @Nonnull String branch,
      @Nonnull String bank,
      @Nonnull String account,
      @Nonnull String assetId
  ) {
    this.account = Objects.requireNonNull(account);
    this.branch = Objects.requireNonNull(branch);
    this.bank = Objects.requireNonNull(bank);
    this.assetId = Objects.requireNonNull(assetId);
  }


  protected boolean canEqual(final Object other) {
    return other instanceof BankAccount;
  }


  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BankAccount other)) {
      return false;
    }
    if (!other.canEqual(this)) {
      return false;
    }
    final Object this$account = this.account;
    final Object other$account = other.account;
    if (!Objects.equals(this$account, other$account)) {
      return false;
    }
    final Object this$assetId = this.assetId;
    final Object other$assetId = other.assetId;
    if (!Objects.equals(this$assetId, other$assetId)) {
      return false;
    }
    final Object this$domain = this.branch;
    final Object other$domain = other.branch;
    if (!Objects.equals(this$domain, other$domain)) {
      return false;
    }
    final Object this$bank = this.bank;
    final Object other$bank = other.bank;
    return Objects.equals(this$bank, other$bank);
  }


  @Nonnull
  public String getAccount() {
    return this.account;
  }


  @Nonnull
  public String getAssetId() {
    return this.assetId;
  }


  @Nonnull
  public String getBank() {
    return this.bank;
  }


  @Nonnull
  public String getDomain() {
    return this.branch;
  }


  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $account = this.account;
    result = result * PRIME + ($account == null ? 43 : $account.hashCode());
    final Object $assetId = this.assetId;
    result = result * PRIME + ($assetId == null ? 43 : $assetId.hashCode());
    final Object $domain = this.branch;
    result = result * PRIME + ($domain == null ? 43 : $domain.hashCode());
    final Object $bank = this.bank;
    result = result * PRIME + ($bank == null ? 43 : $bank.hashCode());
    return result;
  }


  public String toString() {
    return "BankAccount(account=" + this.account + ", assetId=" + this.assetId
        + ", branch=" + this.branch + ", bank=" + this.bank + ")";
  }

}
