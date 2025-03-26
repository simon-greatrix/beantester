package com.pippsford.beantester.factories.basic;

import java.util.random.RandomGenerator;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;

/**
 * Factory that creates random String objects.
 */
public class StringValueFactory extends ValueFactory {

  /** A value factory that only uses the ASCII Alpha-Numeric characters. */
  public static final ValueFactory ALPHANUMERIC;

  /** A value factory that only uses the ASCII printable characters. */
  public static final ValueFactory ASCII;

  /**
   * A value factory that only uses the ISO-8859-15 characters. NOTE: Unlike ISO-8859-1 this has better coverage of western european languages but contains
   * characters from outside the first 256 Unicode code points.
   */
  public static final ValueFactory ISO8859;

  /** A value factory that generates phrases uses the PGP word list. */
  public static final ValueFactory PGP_WORDS = new PgpWordsValueFactory();

  /** A value factory uses characters from across that Unicode characters set. These characters are all printable with Google's Noto Sans font.. */
  public static final ValueFactory UNICODE;

  /** A value factory that only uses the uppercase ASCII characters. */
  public static final ValueFactory UPPERCASE;

  /** A value factory that only uses the URL safe characters. */
  public static final ValueFactory URL_SAFE;

  private static final char[][] CHARSETS = new char[6][];


  /**
   * Create a random string from a random alphabet.
   *
   * @return a random string
   */
  public static String randomString() {
    RandomGenerator random = TestContext.get().getRandom();
    int c = random.nextInt(CHARSETS.length) + 1;
    if (c == CHARSETS.length) {
      int length = 1 + random.nextInt(4);
      return PgpWordsValueFactory.randomWords(length, random);
    }

    char[] charset = CHARSETS[c];
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
    CHARSETS[2] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.~".toCharArray();

    // All printable ASCII;
    int i = 0;
    char[] chars = new char[95];
    for (int j = 32; j < 127; j++) {
      chars[i++] = (char) j;
    }
    CHARSETS[3] = chars;

    // All ISO-8859-15 characters
    CHARSETS[4] = ("!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz"
        + "{|}~¡¢£€¥Š§š©ª«¬®¯°±²³Žµ¶·ž¹º»ŒœŸ¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ")
        .toCharArray();

    /*
     * The selection criteria for the alphabet is:
     *
     * The character type is one of:
     *    TITLECASE_LETTER
     *    UPPERCASE_LETTER
     *    LOWERCASE_LETTER
     *    OTHER_LETTER
     *
     *    DECIMAL_DIGIT_NUMBER
     *    LETTER_NUMBER
     *    OTHER_NUMBER
     *
     *    START_PUNCTUATION
     *    END_PUNCTUATION
     *    INITIAL_QUOTE_PUNCTUATION
     *    FINAL_QUOTE_PUNCTUATION
     *    CONNECTOR_PUNCTUATION
     *    DASH_PUNCTUATION
     *    OTHER_PUNCTUATION
     *
     *    CURRENCY_SYMBOL
     *    ENCLOSING_MARK
     *    MATH_SYMBOL
     *    OTHER_SYMBOL
     *
     * The directionality is one of:
     *    DIRECTIONALITY_LEFT_TO_RIGHT
     *    DIRECTIONALITY_COMMON_NUMBER_SEPARATOR
     *    DIRECTIONALITY_EUROPEAN_NUMBER
     *    DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR
     *    DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR
     *    DIRECTIONALITY_OTHER_NEUTRALS
     *
     * This leaves 51802 code points.
     *
     * For display purposes, we also require that the Google "Noto Sans" font can display the character. This reduces the available number of code points to
     * only 1543, which is still more than 16 times what is available in ASCII.
     */
    CHARSETS[5] = new char[1543];
    int[][] ranges = {
        {33, 94}, {95, 96}, {97, 127}, {161, 168}, {169, 173},
        {174, 175}, {176, 180}, {181, 184}, {185, 592}, {891, 895},
        {902, 907}, {908, 909}, {910, 930}, {931, 975}, {976, 1155},
        {1162, 1320}, {2308, 2362}, {2365, 2366}, {2384, 2385}, {2392, 2402},
        {2404, 2417}, {2418, 2419}, {2427, 2432}, {7680, 7836}, {7838, 7839},
        {7840, 7930}, {7936, 7958}, {7960, 7966}, {7968, 8006},
        {8008, 8014}, {8016, 8024}, {8025, 8026}, {8027, 8028},
        {8029, 8030}, {8031, 8062}, {8064, 8117}, {8118, 8125},
        {8126, 8127}, {8130, 8133}, {8134, 8141}, {8144, 8148},
        {8150, 8156}, {8160, 8173}, {8178, 8181}, {8182, 8189},
        {8211, 8213}, {8216, 8219}, {8220, 8223}, {8226, 8227},
        {8230, 8231}, {8249, 8251}, {8260, 8261}, {8308, 8309},
        {8352, 8362}, {8363, 8374}, {8377, 8379}, {8470, 8471},
        {8722, 8723}, {8725, 8726}, {9676, 9677}, {11360, 11374},
        {11377, 11384}, {42891, 42893}
    };
    i = 0;
    for (int[] range : ranges) {
      for (int k = range[0]; k < range[1]; k++) {
        CHARSETS[5][i++] = (char) k;
      }
    }

    UPPERCASE = new ValueFactory(String.class, false, () -> "X", () -> "Y", () -> randomString(CHARSETS[0]));
    ALPHANUMERIC = new ValueFactory(String.class, false, () -> "x", () -> "y", () -> randomString(CHARSETS[1]));
    URL_SAFE = new ValueFactory(String.class, false, () -> "x", () -> "y", () -> randomString(CHARSETS[2]));
    ASCII = new ValueFactory(String.class, false, () -> "x", () -> "y", () -> randomString(CHARSETS[3]));
    ISO8859 = new ValueFactory(String.class, false, () -> "x", () -> "y", () -> randomString(CHARSETS[4]));
    UNICODE = new ValueFactory(String.class, false, () -> "x", () -> "y", () -> randomString(CHARSETS[5]));
  }


  /**
   * Construct a new String object factory. Note the primary and secondary values are "x" and "y" respectively.
   */
  public StringValueFactory() {
    super(String.class, () -> "x", () -> "y", StringValueFactory::randomString);
  }

}
