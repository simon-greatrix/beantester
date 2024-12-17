package io.setl.beantester.example;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Nonnull;

/**
 * A link in a transfer.
 */
public class ApproverLink {

  public static class ApproverLinkBuilder {

    private ApproverAccount account;

    private AccountAction action;

    private int index;

    private boolean tags$set;

    private Set<String> tags$value;


    ApproverLinkBuilder() {
    }


    public ApproverLinkBuilder account(ApproverAccount account) {
      this.account = account;
      return this;
    }


    public ApproverLinkBuilder action(AccountAction action) {
      this.action = action;
      return this;
    }


    public ApproverLink build() {
      Set<String> tags$value = this.tags$value;
      if (!this.tags$set) {
        tags$value = ApproverLink.$default$tags();
      }
      return new ApproverLink(this.account, this.action, this.index, tags$value);
    }


    public ApproverLinkBuilder index(int index) {
      this.index = index;
      return this;
    }


    public ApproverLinkBuilder tags(Set<String> tags) {
      this.tags$value = tags;
      this.tags$set = true;
      return this;
    }


    public String toString() {
      return "ApproverLink.ApproverLinkBuilder(account=" + this.account + ", action=" + this.action + ", index=" + this.index + ", tags$value="
          + this.tags$value + ")";
    }

  }


  private static Set<String> $default$tags() {
    return new HashSet<>();
  }


  public static ApproverLinkBuilder builder() {
    return new ApproverLinkBuilder();
  }


  @Nonnull
  private final ApproverAccount account;

  @Nonnull
  private final AccountAction action;

  /** The index of this link in the transfer. */
  private final int index;

  /** Tags assigned to this link. */
  private Set<String> tags = new HashSet<>();


  ApproverLink(@Nonnull ApproverAccount account, @Nonnull AccountAction action, int index, Set<String> tags) {
    this.account = account;
    this.action = action;
    this.index = index;
    this.tags = tags;
  }


  protected boolean canEqual(final Object other) {
    return other instanceof ApproverLink;
  }


  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ApproverLink other)) {
      return false;
    }
    if (!other.canEqual(this)) {
      return false;
    }
    final Object this$account = this.getAccount();
    final Object other$account = other.getAccount();
    if (!Objects.equals(this$account, other$account)) {
      return false;
    }
    final Object this$action = this.getAction();
    final Object other$action = other.getAction();
    if (!Objects.equals(this$action, other$action)) {
      return false;
    }
    if (this.getIndex() != other.getIndex()) {
      return false;
    }
    final Object this$tags = this.getTags();
    final Object other$tags = other.getTags();
    return Objects.equals(this$tags, other$tags);
  }


  @Nonnull
  public ApproverAccount getAccount() {
    return this.account;
  }


  @Nonnull
  public AccountAction getAction() {
    return this.action;
  }


  public int getIndex() {
    return this.index;
  }


  public Set<String> getTags() {
    return this.tags;
  }


  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $account = this.getAccount();
    result = result * PRIME + ($account == null ? 43 : $account.hashCode());
    final Object $action = this.getAction();
    result = result * PRIME + ($action == null ? 43 : $action.hashCode());
    result = result * PRIME + this.getIndex();
    final Object $tags = this.getTags();
    result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
    return result;
  }


  public void setTags(Set<String> tags) {
    this.tags = tags;
  }


  public String toString() {
    return "ApproverLink(account=" + this.getAccount() + ", action=" + this.getAction() + ", index=" + this.getIndex() + ", tags=" + this.getTags() + ")";
  }

}
