package io.setl.beantester.factories.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.random.RandomGenerator;

import io.setl.beantester.factories.basic.RandomFactoryBase;

public class UrlFactory extends RandomFactoryBase<URL> {

  private final List<String> paths = Arrays.asList("", "/foo", "/foo/bar/", "/foo/bar/index?a=b");

  private final RandomValueSampler sampler;

  private final List<String> schemes = Arrays.asList("http://", "https://", "ftp://");

  private final List<String> tlds = Arrays.asList(".example", ".invalid", ".test");


  public UrlFactory(RandomGenerator randomValueGenerator) {
    super(randomValueGenerator);
    this.sampler = new RandomValueSampler(randomValueGenerator);
  }


  @Override
  public URL create() {
    String scheme = sampler.getFrom(schemes);
    String domain = getRandomDomain();
    String tld = sampler.getFrom(tlds);
    String path = sampler.getFrom(paths);
    String url = String.join("", scheme, domain, tld, path);
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new IllegalStateException(url, e);
    }
  }


  protected String getRandomDomain() {
    int subdomainCount = getRandomGenerator().nextInt(1) + 1;
    return IntStream.range(0, subdomainCount)
        .mapToObj(num -> UUID.randomUUID().toString())
        .collect(Collectors.joining("."));
  }

}
