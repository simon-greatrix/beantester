package io.setl.beantester.factories.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.ValueFactoryRepository;

public final class NetFactories {

  public static void load(TestContext context, ValueFactoryRepository repository) {
    RandomGenerator random = context.getRandom();
    UrlValueFactory urlFactory = new UrlValueFactory(random);
    repository.addFactory(URL.class, urlFactory);
    repository.addFactory(URI.class, (t) -> {
      try {
        return urlFactory.create(t).toURI();
      } catch (URISyntaxException e) {
        try {
          return new URI("example:" + Long.toString(random.nextLong(0x4000_0000_0000_0000L), 36));
        } catch (URISyntaxException e2) {
          throw new InternalError("Failed to create URI", e2);
        }
      }
    });
  }


}
