package io.setl.beantester.example;


/**
 * An account in a transfer.
 */
public class ApproverAccount {


  @NotNull
  private final String account;


  @NotNull
  private final String assetId;


  @NotNull
  private final String domain;


  @NotNull
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
      String domain,
      String participant,
      String account,
      String assetId
  ) {
    this.account = account;
    this.domain = domain;
    this.participant = participant;
    this.assetId = assetId;
  }


  protected boolean canEqual(final Object other) {
    return other instanceof ApproverAccount;
  }


  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ApproverAccount)) {
      return false;
    }
    final ApproverAccount other = (ApproverAccount) o;
    if (!other.canEqual((Object) this)) {
      return false;
    }
    final Object this$account = this.account;
    final Object other$account = other.account;
    if (this$account == null ? other$account != null : !this$account.equals(other$account)) {
      return false;
    }
    final Object this$assetId = this.assetId;
    final Object other$assetId = other.assetId;
    if (this$assetId == null ? other$assetId != null : !this$assetId.equals(other$assetId)) {
      return false;
    }
    final Object this$domain = this.domain;
    final Object other$domain = other.domain;
    if (this$domain == null ? other$domain != null : !this$domain.equals(other$domain)) {
      return false;
    }
    final Object this$participant = this.participant;
    final Object other$participant = other.participant;
    if (this$participant == null ? other$participant != null : !this$participant.equals(other$participant)) {
      return false;
    }
    return true;
  }


  @NotNull
  public String getAccount() {
    return this.account;
  }


  @NotNull
  public String getAssetId() {
    return this.assetId;
  }



  @NotNull
  public String getDomain() {
    return this.domain;
  }



  @NotNull
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
