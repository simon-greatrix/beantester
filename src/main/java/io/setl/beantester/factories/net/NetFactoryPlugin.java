package io.setl.beantester.factories.net;

import java.net.URI;
import java.net.URL;

import java.util.random.RandomGenerator;

import io.setl.beantester.factories.FactoryCollection;
import io.setl.beantester.factories.FactoryCollectionPlugin;

public final class NetFactoryPlugin implements FactoryCollectionPlugin {

  @Override
  public void initialize(FactoryCollection factoryCollection, RandomGenerator randomValueGenerator) {
    UrlFactory urlFactory = new UrlFactory(randomValueGenerator);
    factoryCollection.addFactory(URL.class, urlFactory);
    factoryCollection.addFactory(URI.class, () -> URI.create(urlFactory.create().toString()));
  }

}
