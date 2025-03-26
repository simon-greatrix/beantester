package com.pippsford.beantester.factories.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.random.RandomGenerator;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.FactoryRepository;

/**
 * Load the network factories.
 */
public final class NetFactories {

  /**
   * Load the network factories.
   *
   * @param repository the repository to load the factories into
   */
  public static void load(FactoryRepository repository) {
    UrlValueFactory urlFactory = new UrlValueFactory();
    repository.addFactory(urlFactory);
    repository.addFactory(new ValueFactory(
        URI.class,
        (t) -> {
          try {
            return ((URL) urlFactory.create(t)).toURI();
          } catch (URISyntaxException e) {
            try {
              RandomGenerator random = TestContext.get().getRandom();
              return new URI("example:" + Long.toString(random.nextLong(0x4000_0000_0000_0000L), 36));
            } catch (URISyntaxException e2) {
              throw new InternalError("Failed to create URI", e2);
            }
          }
        }
    ));
  }


}
