package com.pippsford.beantester.sample.beans;


import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * An RFC-945 standard error response.
 */
@Value
@Builder
public class ErrorResponse {

  /** Prefix for the URI specifying the error type. */
  public static final String TYPE_ERROR_PREFIX = "urn:eeze:";

  /** Human-readable explanation specific to this occurrence of the problem. */
  String detail;

  /** A URI reference that identifies the specific occurrence of the problem. */
  String instance;

  /** A JSON object containing key/value pairs of information. */
  @Singular
  Map<String, Object> parameters;

  /** The HTTP status code. */
  int status;

  @Default
  String timestamp = Instant.now().toString();

  /** A short human-readable summary of the problem. */
  String title;

  /** A URI reference that identifies the problem type. */
  String type;


  public Map<String, Object> getParameters() {
    return parameters;
  }

}
