package io.setl.beantester.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Nonnull;

/** An example of a bean with a builder. The original was created by Lombok. */
public class BuildableBean {


  public static class BuildableBeanBuilder {

    private BigDecimal amount;

    private String assetId;

    private String correlationId;

    private String from;

    private int index;

    private ArrayList<String> links;

    private boolean tags$set;

    private Set<String> tags$value;

    private String to;

    private String type;


    BuildableBeanBuilder() {
    }


    public BuildableBeanBuilder amount(@Nonnull BigDecimal amount) {
      this.amount = amount;
      return this;
    }


    public BuildableBeanBuilder assetId(@Nonnull String assetId) {
      this.assetId = assetId;
      return this;
    }


    public BuildableBean build() {
      List<String> links = switch (this.links == null ? 0 : this.links.size()) {
        case 0 -> Collections.emptyList();
        case 1 -> Collections.singletonList(this.links.get(0));
        default -> Collections.unmodifiableList(new ArrayList<String>(this.links));
      };
      Set<String> tags$value = this.tags$value;
      if (!this.tags$set) {
        tags$value = BuildableBean.$default$tags();
      }
      return new BuildableBean(this.amount, this.assetId, this.correlationId, this.from, this.index, links, this.to, this.type, tags$value);
    }


    public BuildableBeanBuilder clearLinks() {
      if (this.links != null) {
        this.links.clear();
      }
      return this;
    }


    public BuildableBeanBuilder correlationId(String correlationId) {
      this.correlationId = correlationId;
      return this;
    }


    public BuildableBeanBuilder from(String from) {
      this.from = from;
      return this;
    }


    public BuildableBeanBuilder index(int index) {
      this.index = index;
      return this;
    }


    public BuildableBeanBuilder link(String link) {
      if (this.links == null) {
        this.links = new ArrayList<String>();
      }
      this.links.add(link);
      return this;
    }


    public BuildableBeanBuilder links(@Nonnull Collection<String> links) {
      if (links == null) {
        throw new NullPointerException("links cannot be null");
      }
      if (this.links == null) {
        this.links = new ArrayList<String>();
      }
      this.links.addAll(links);
      return this;
    }


    public BuildableBeanBuilder tags(Set<String> tags) {
      this.tags$value = tags;
      this.tags$set = true;
      return this;
    }


    public BuildableBeanBuilder to(String to) {
      this.to = to;
      return this;
    }


    public String toString() {
      return "ApproverTransfer.ApproverTransferBuilder(amount=" + this.amount + ", assetId=" + this.assetId + ", correlationId=" + this.correlationId
          + ", from=" + this.from + ", index=" + this.index + ", links=" + this.links + ", to=" + this.to + ", type=" + this.type + ", tags$value="
          + this.tags$value + ")";
    }


    public BuildableBeanBuilder type(@Nonnull String type) {
      this.type = type;
      return this;
    }

  }


  private static Set<String> $default$tags() {
    return new HashSet<>();
  }


  public static BuildableBeanBuilder builder() {
    return new BuildableBeanBuilder();
  }


  /** The amount this transaction applies to the account. */
  @Nonnull
  private final BigDecimal amount;

  /** The asset ID. */
  @Nonnull
  private final String assetId;

  /** The transfer's correlation ID. */
  private final String correlationId;

  /** The 'from' account. */
  private final String from;

  /** The index of this transfer in the manifest. */
  private final int index;

  /** Links that constitute this transfer. */
  @Nonnull
  private final List<String> links;

  /** The 'to' account. */
  private final String to;

  /** The type of the transfer. */
  @Nonnull
  private final String type;

  /** Tags assigned to this transfer. */
  private Set<String> tags = new HashSet<>();


  public BuildableBean(
      @Nonnull BigDecimal amount,
      @Nonnull String assetId,
      String correlationId,
      String from,
      int index,
      @Nonnull List<String> links,
      String to,
      @Nonnull String type,
      Set<String> tags
  ) {
    this.amount = amount;
    this.assetId = assetId;
    this.correlationId = correlationId;
    this.from = from;
    this.index = index;
    this.links = links;
    this.to = to;
    this.type = type;
    this.tags = tags;
  }


  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BuildableBean other)) {
      return false;
    }
    final Object this$amount = this.getAmount();
    final Object other$amount = other.getAmount();
    if (!Objects.equals(this$amount, other$amount)) {
      return false;
    }
    final Object this$assetId = this.getAssetId();
    final Object other$assetId = other.getAssetId();
    if (!Objects.equals(this$assetId, other$assetId)) {
      return false;
    }
    final Object this$correlationId = this.getCorrelationId();
    final Object other$correlationId = other.getCorrelationId();
    if (!Objects.equals(this$correlationId, other$correlationId)) {
      return false;
    }
    final Object this$from = this.getFrom();
    final Object other$from = other.getFrom();
    if (!Objects.equals(this$from, other$from)) {
      return false;
    }
    if (this.getIndex() != other.getIndex()) {
      return false;
    }
    final Object this$links = this.getLinks();
    final Object other$links = other.getLinks();
    if (!Objects.equals(this$links, other$links)) {
      return false;
    }
    final Object this$to = this.getTo();
    final Object other$to = other.getTo();
    if (!Objects.equals(this$to, other$to)) {
      return false;
    }
    final Object this$type = this.getType();
    final Object other$type = other.getType();
    if (!Objects.equals(this$type, other$type)) {
      return false;
    }
    final Object this$tags = this.getTags();
    final Object other$tags = other.getTags();
    return Objects.equals(this$tags, other$tags);
  }


  @Nonnull
  public BigDecimal getAmount() {
    return this.amount;
  }


  @Nonnull
  public String getAssetId() {
    return this.assetId;
  }


  public String getCorrelationId() {
    return this.correlationId;
  }


  public String getFrom() {
    return this.from;
  }


  public int getIndex() {
    return this.index;
  }


  @Nonnull
  public List<String> getLinks() {
    return this.links;
  }


  public Set<String> getTags() {
    return this.tags;
  }


  public String getTo() {
    return this.to;
  }


  @Nonnull
  public String getType() {
    return this.type;
  }


  /**
   * Does this transfer have a "from" account?
   *
   * @return true if a "from" account is present
   */
  public boolean hasFrom() {
    return from != null;
  }


  /**
   * Does this transfer have a "to" account?
   *
   * @return true if a "to" account is present
   */
  public boolean hasTo() {
    return to != null;
  }


  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $amount = this.getAmount();
    result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
    final Object $assetId = this.getAssetId();
    result = result * PRIME + ($assetId == null ? 43 : $assetId.hashCode());
    final Object $correlationId = this.getCorrelationId();
    result = result * PRIME + ($correlationId == null ? 43 : $correlationId.hashCode());
    final Object $from = this.getFrom();
    result = result * PRIME + ($from == null ? 43 : $from.hashCode());
    result = result * PRIME + this.getIndex();
    final Object $links = this.getLinks();
    result = result * PRIME + ($links == null ? 43 : $links.hashCode());
    final Object $to = this.getTo();
    result = result * PRIME + ($to == null ? 43 : $to.hashCode());
    final Object $type = this.getType();
    result = result * PRIME + ($type == null ? 43 : $type.hashCode());
    final Object $tags = this.getTags();
    result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
    return result;
  }


  public void setTags(Set<String> tags) {
    this.tags = tags;
  }


  public String toString() {
    return "BuildableBean(amount=" + this.getAmount() + ", assetId=" + this.getAssetId() + ", correlationId=" + this.getCorrelationId() + ", from="
        + this.getFrom() + ", index=" + this.getIndex() + ", links=" + this.getLinks() + ", to=" + this.getTo() + ", type=" + this.getType() + ", tags="
        + this.getTags() + ")";
  }

}

