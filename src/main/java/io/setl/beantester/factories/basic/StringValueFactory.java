package io.setl.beantester.factories.basic;

import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * Concrete Factory that creates random String objects.
 */
public class StringValueFactory extends ValueFactory {

  public static final ValueFactory ALPHANUMERIC;

  public static final ValueFactory ASCII;

  public static final ValueFactory ISO88591;

  public static final ValueFactory UPPERCASE;

  private static final char[][] CHARSETS = new char[4][];


  /**
   * Create a random string from a random alphabet.
   *
   * @return a random string
   */
  public static String randomString() {
    RandomGenerator random = TestContext.get().getRandom();
    char[] charset = CHARSETS[random.nextInt(CHARSETS.length)];
    return randomString(random, charset);
  }


  /**
   * Create a random string from the specified alphabet.
   *
   * @param source the alphabet to use
   *
   * @return a random string
   */
  public static String randomString(char[] source) {
    RandomGenerator random = TestContext.get().getRandom();
    return randomString(random, source);
  }


  private static String randomString(RandomGenerator random, char[] source) {
    int length = random.nextInt(8) + 3;
    char[] chars = new char[length];
    for (int i = 0; i < length; i++) {
      chars[i] = source[random.nextInt(source.length)];
    }
    return new String(chars);
  }


  static {
    CHARSETS[0] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    CHARSETS[1] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    // All printable ASCII;
    int i = 0;
    char[] chars = new char[95];
    for (int j = 32; j < 127; j++) {
      chars[i++] = (char) j;
    }
    CHARSETS[2] = chars;

    // All ISO-8859-1 characters
    chars = new char[191];
    i = 0;
    for (int j = 32; j < 127; j++) {
      chars[i++] = (char) j;
    }
    for (int j = 160; j < 256; j++) {
      chars[i++] = (char) j;
    }
    CHARSETS[3] = chars;

    UPPERCASE = new ValueFactory(String.class, () -> "x", () -> "y", () -> randomString(CHARSETS[0]));
    ALPHANUMERIC = new ValueFactory(String.class, () -> "x", () -> "y", () -> randomString(CHARSETS[1]));
    ASCII = new ValueFactory(String.class, () -> "x", () -> "y", () -> randomString(CHARSETS[2]));
    ISO88591 = new ValueFactory(String.class, () -> "x", () -> "y", () -> randomString(CHARSETS[3]));
  }


  /**
   * Construct a new String object factory.
   */
  public StringValueFactory() {
    super(String.class, () -> "x", () -> "y", StringValueFactory::randomString);
  }

}
