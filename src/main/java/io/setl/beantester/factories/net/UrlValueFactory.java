package io.setl.beantester.factories.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.Sampler;

/** Factory for URLs. */
public class UrlValueFactory extends ValueFactory {

  private static final List<String> DOMAINS = Arrays.asList(".example", ".invalid", ".test");

  private static final URL PRIMARY;

  private static final List<String> SCHEMES = Arrays.asList("http://", "https://", "ftp://");

  private static final URL SECONDARY;


  protected static URL createRandom() {
    RandomGenerator random = TestContext.get().getRandom();
    String scheme = Sampler.getFrom(SCHEMES);
    String domain = generate(random, 1, 2, ".");
    String tld = Sampler.getFrom(DOMAINS);
    String path = generate(random, 0, 4, "/");
    if (!path.isBlank() || random.nextBoolean()) {
      path = "/" + path;
    }
    String url = scheme + domain + tld + path;
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new IllegalStateException(url, e);
    }
  }


  private static String generate(RandomGenerator random, int min, int max, String delim) {
    int count = random.nextInt(min, max);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      if (i > 0) {
        sb.append(delim);
      }
      sb.append(word(random));
    }
    return sb.toString();
  }


  private static String word(RandomGenerator random) {
    int val = random.nextInt(1, 11881376);
    StringBuilder sb = new StringBuilder();
    while (val > 0) {
      sb.append((char) ('a' + (val % 26)));
      val /= 26;
    }
    return sb.toString();
  }


  static {
    try {
      PRIMARY = new URL("http://localhost/primary");
      SECONDARY = new URL("http://localhost/secondary");
    } catch (MalformedURLException e) {
      throw new ExceptionInInitializerError(e);
    }
  }


  public UrlValueFactory() {
    super(URL.class, () -> PRIMARY, () -> SECONDARY, UrlValueFactory::createRandom);
  }

}
