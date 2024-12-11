package io.setl.beantester.util;

/**
 * Utility methods for assertions.
 *
 * @author Graham Williamson
 */
public final class AssertionUtils {

  /**
   * Fail an assertion, which will throw an <code>AssertionError</code> with no message.
   */
  public static void fail() {
    throw new AssertionError();
  }


  /**
   * Fail an assertion, which will throw an <code>AssertionError</code> with the specified message.
   *
   * @param message A message detailing the assertion failure.
   */
  public static void fail(String message) {
    throw new AssertionError(message);
  }


  /**
   * Construct a new AssertionUtils.
   */
  private AssertionUtils() {
    // Do nothing - make non-instantiable
  }

}
