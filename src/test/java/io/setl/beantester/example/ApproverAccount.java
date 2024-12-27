package io.setl.beantester.example;


import java.util.Objects;
import jakarta.annotation.Nonnull;

/**
 * An account in a transfer.
 */
public class ApproverAccount {


  @Nonnull
  private final String account;


  @Nonnull
  private final String assetId;


  @Nonnull
  private final String domain;


  @Nonnull
  private final String participant;




  /**
   * New instance.
   *
   * @param account     the account
   * @param domain      the domain
   * @param participant the participant
   * @param assetId     the asset
   */
  public ApproverAccount(
      @Nonnull String domain,
      @Nonnull String participant,
      @Nonnull String account,
      @Nonnull String assetId
  ) {
    this.account = Objects.requireNonNull(account);
    this.domain = Objects.requireNonNull(domain);
    this.participant = Objects.requireNonNull(participant);
    this.assetId = Objects.requireNonNull(assetId);
  }


  protected boolean canEqual(final Object other) {
    return other instanceof ApproverAccount;
  }


  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ApproverAccount other)) {
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
    final Object this$domain = this.domain;
    final Object other$domain = other.domain;
    if (!Objects.equals(this$domain, other$domain)) {
      return false;
    }
    final Object this$participant = this.participant;
    final Object other$participant = other.participant;
    return Objects.equals(this$participant, other$participant);
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
  public String getDomain() {
    return this.domain;
  }



  @Nonnull
  public String getParticipant() {
    return this.participant;
  }


  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $account = this.account;
    result = result * PRIME + ($account == null ? 43 : $account.hashCode());
    final Object $assetId = this.assetId;
    result = result * PRIME + ($assetId == null ? 43 : $assetId.hashCode());
    final Object $domain = this.domain;
    result = result * PRIME + ($domain == null ? 43 : $domain.hashCode());
    final Object $participant = this.participant;
    result = result * PRIME + ($participant == null ? 43 : $participant.hashCode());
    return result;
  }


  public String toString() {
    return "ApproverAccount(account=" + this.account + ", assetId=" + this.assetId
        + ", domain=" + this.domain + ", participant=" + this.participant + ")";
  }

}
