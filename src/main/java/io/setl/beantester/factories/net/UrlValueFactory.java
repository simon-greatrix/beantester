package io.setl.beantester.factories.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.Sampler;
import io.setl.beantester.factories.basic.RandomValueFactoryBase;

/** Factory for URLs. */
public class UrlValueFactory extends RandomValueFactoryBase<URL> {

  private static final URL PRIMARY;

  private static final List<String> SCHEMES = Arrays.asList("http://", "https://", "ftp://");

  private static final URL SECONDARY;

  private static final List<String> TLDS = Arrays.asList(".example", ".invalid", ".test");

  static {
    try {
      PRIMARY = new URL("http://localhost/primary");
      SECONDARY = new URL("http://localhost/secondary");
    } catch (MalformedURLException e) {
      throw new ExceptionInInitializerError(e);
    }
  }


  public UrlValueFactory(RandomGenerator random) {
    super(random);
  }


  @Override
  protected URL createPrimary() {
    return PRIMARY;
  }


  @Override
  protected URL createRandom() {
    RandomGenerator random = getRandom();
    String scheme = Sampler.getFrom(random, SCHEMES);
    String domain = generate(1, 2, ".");
    String tld = Sampler.getFrom(random, TLDS);
    String path = generate(0, 4, "/");
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


  @Override
  protected URL createSecondary() {
    return SECONDARY;
  }


  protected String generate(int min, int max, String delim) {
    RandomGenerator random = getRandom();
    int count = random.nextInt(min, max);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      if (i > 0) {
        sb.append(delim);
      }
      sb.append(word());
    }
    return sb.toString();
  }


  private String word() {
    RandomGenerator random = getRandom();
    int val = random.nextInt(1, 11881376);
    StringBuilder sb = new StringBuilder();
    while (val > 0) {
      sb.append((char) ('a' + (val % 26)));
      val /= 26;
    }
    return sb.toString();
  }

}
