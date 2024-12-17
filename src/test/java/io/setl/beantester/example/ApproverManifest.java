package io.setl.beantester.example;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Nonnull;

/** The manifest of a set of transfers. */
public final class ApproverManifest {

  public static class ApproverManifestBuilder {

    private String correlationId;

    private String requestId;

    private String swarmId;

    private boolean tags$set;

    private Set<String> tags$value;

    private List<ApproverTransfer> transfers;


    ApproverManifestBuilder() {
    }


    public ApproverManifest build() {
      Set<String> tags$value = this.tags$value;
      if (!this.tags$set) {
        tags$value = ApproverManifest.$default$tags();
      }
      return new ApproverManifest(this.correlationId, this.requestId, this.swarmId, this.transfers, tags$value);
    }


    public ApproverManifestBuilder correlationId(String correlationId) {
      this.correlationId = correlationId;
      return this;
    }


    public ApproverManifestBuilder requestId(String requestId) {
      this.requestId = requestId;
      return this;
    }


    public ApproverManifestBuilder swarmId(@Nonnull String swarmId) {
      this.swarmId = swarmId;
      return this;
    }


    public ApproverManifestBuilder tags(Set<String> tags) {
      this.tags$value = tags;
      this.tags$set = true;
      return this;
    }


    public String toString() {
      return "ApproverManifest.ApproverManifestBuilder(correlationId=" + this.correlationId + ", requestId=" + this.requestId + ", swarmId=" + this.swarmId
          + ", transfers=" + this.transfers + ", tags$value=" + this.tags$value + ")";
    }


    public ApproverManifestBuilder transfers(List<ApproverTransfer> transfers) {
      this.transfers = transfers;
      return this;
    }

  }


  private static Set<String> $default$tags() {
    return new HashSet<>();
  }


  public static ApproverManifestBuilder builder() {
    return new ApproverManifestBuilder();
  }


  /** Correlation ID assigned by the client, if any. */
  private final String correlationId;

  /** Request ID assigned by the scheduler. */
  private final String requestId;

  /** Swarm ID assigned by the scheduler. */
  @Nonnull
  private final String swarmId;

  /** Transfers associated with this manifest. */
  @Nonnull
  private final List<ApproverTransfer> transfers;

  /** Tags assigned to this manifest. */
  private Set<String> tags = new HashSet<>();


  ApproverManifest(String correlationId, String requestId, @Nonnull String swarmId, @Nonnull List<ApproverTransfer> transfers, Set<String> tags) {
    this.correlationId = correlationId;
    this.requestId = requestId;
    this.swarmId = swarmId;
    this.transfers = transfers;
    this.tags = tags;
  }


  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ApproverManifest other)) {
      return false;
    }
    final Object this$correlationId = this.getCorrelationId();
    final Object other$correlationId = other.getCorrelationId();
    if (!Objects.equals(this$correlationId, other$correlationId)) {
      return false;
    }
    final Object this$requestId = this.getRequestId();
    final Object other$requestId = other.getRequestId();
    if (!Objects.equals(this$requestId, other$requestId)) {
      return false;
    }
    final Object this$swarmId = this.getSwarmId();
    final Object other$swarmId = other.getSwarmId();
    if (!Objects.equals(this$swarmId, other$swarmId)) {
      return false;
    }
    final Object this$transfers = this.getTransfers();
    final Object other$transfers = other.getTransfers();
    if (!Objects.equals(this$transfers, other$transfers)) {
      return false;
    }
    final Object this$tags = this.getTags();
    final Object other$tags = other.getTags();
    return Objects.equals(this$tags, other$tags);
  }


  public String getCorrelationId() {
    return this.correlationId;
  }


  public String getRequestId() {
    return this.requestId;
  }


  @Nonnull
  public String getSwarmId() {
    return this.swarmId;
  }


  public Set<String> getTags() {
    return this.tags;
  }


  @Nonnull
  public List<ApproverTransfer> getTransfers() {
    return this.transfers;
  }


  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $correlationId = this.getCorrelationId();
    result = result * PRIME + ($correlationId == null ? 43 : $correlationId.hashCode());
    final Object $requestId = this.getRequestId();
    result = result * PRIME + ($requestId == null ? 43 : $requestId.hashCode());
    final Object $swarmId = this.getSwarmId();
    result = result * PRIME + ($swarmId == null ? 43 : $swarmId.hashCode());
    final Object $transfers = this.getTransfers();
    result = result * PRIME + ($transfers == null ? 43 : $transfers.hashCode());
    final Object $tags = this.getTags();
    result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
    return result;
  }


  public void setTags(Set<String> tags) {
    this.tags = tags;
  }


  public String toString() {
    return "ApproverManifest(correlationId=" + this.getCorrelationId() + ", requestId=" + this.getRequestId() + ", swarmId=" + this.getSwarmId()
        + ", transfers=" + this.getTransfers() + ", tags=" + this.getTags() + ")";
  }

}
