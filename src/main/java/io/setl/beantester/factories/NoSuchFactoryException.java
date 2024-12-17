package io.setl.beantester.factories;

import java.io.Serial;

/**
 * An exception that may be thrown when trying to get an unknown or unregistered Factory from the FactoryRepository.
 *
 * @author Graham Williamson
 */
public class NoSuchFactoryException extends RuntimeException {

  /** Unique version ID of this Serializable class. */
  @Serial
  private static final long serialVersionUID = 1L;


  /**
   * Construct a new No Such Factory Exception with the specified message.
   *
   * @param message A human-readable String message describing the problem that occurred.
   */
  public NoSuchFactoryException(String message) {
    super(message);
  }


  /**
   * Construct a new No Such Factory Exception with the specified message.
   *
   * @param message A human-readable String message describing the problem that occurred.
   * @param cause   The Throwable that caused this exception to be thrown.
   */
  public NoSuchFactoryException(String message, Throwable cause) {
    super(message, cause);
  }

}
