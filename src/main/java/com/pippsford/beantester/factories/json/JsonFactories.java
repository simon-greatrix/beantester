package com.pippsford.beantester.factories.json;

import com.pippsford.beantester.factories.FactoryRepository;

public class JsonFactories {

  /**
   * Load the JSON factories.
   *
   * @param repository the repository to load the factories into
   */
  public static void load(FactoryRepository repository) {
    JsonSingletonFactory singletonFactory = new JsonSingletonFactory();
    JsonArrayFactory arrayFactory = new JsonArrayFactory(singletonFactory);
    JsonObjectFactory objectFactory = new JsonObjectFactory(singletonFactory);
    JsonNumberFactory numberFactory = new JsonNumberFactory();
    JsonStringFactory stringFactory = new JsonStringFactory();

    JsonValueFactory valueFactory = new JsonValueFactory(
        arrayFactory,
        objectFactory,
        numberFactory,
        stringFactory,
        singletonFactory
    );

    arrayFactory.setValueFactory(valueFactory);
    objectFactory.setValueFactory(valueFactory);

    repository.addFactory(arrayFactory);
    repository.addFactory(objectFactory);
    repository.addFactory(numberFactory);
    repository.addFactory(stringFactory);
    repository.addFactory(singletonFactory);

  }

}
