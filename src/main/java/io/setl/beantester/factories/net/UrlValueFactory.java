package io.setl.beantester.factories.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.Sampler;
import io.setl.beantester.factories.basic.RandomValueFactoryBase;

public class UrlValueFactory extends RandomValueFactoryBase<URL> {

  private static final List<String> PATHS = Arrays.asList("", "/foo", "/foo/bar/", "/foo/bar/index?a=b");

  private static final List<String> SCHEMES = Arrays.asList("http://", "https://", "ftp://");

  private static final List<String> TLDS = Arrays.asList(".example", ".invalid", ".test");


  public UrlValueFactory(RandomGenerator random) {
    super(random);
  }


  @Override
  public URL create() {
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


  protected String generate(int min, int max, String delim) {
    RandomGenerator random = getRandom();
    int count = random.nextInt(min, max);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      if (i > 0) {
        sb.append(delim);
      }
      sb.append(Integer.toString(random.nextInt(0x4000_0000), 36));
    }
    return sb.toString();
  }

}
