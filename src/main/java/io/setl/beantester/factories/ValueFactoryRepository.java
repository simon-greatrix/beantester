package io.setl.beantester.factories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.basic.BasicFactories;
import io.setl.beantester.factories.io.FileFactories;
import io.setl.beantester.factories.net.NetFactories;
import io.setl.beantester.factories.time.TimeFactories;
import io.setl.beantester.factories.util.UtilFactories;

public class ValueFactoryRepository {

  private final HashMap<Class<?>, ValueFactory<?>> factories = new HashMap<>();

  private final List<FactoryLookup> factoryLookups = new ArrayList<>();


  /**
   * Add the specified Factory to the collection. If a Factory is already registered against the specified class, the existing registered Factory will be
   * replaced
   * with the Factory you specify here.
   *
   * @param clazz        The type of objects the Factory creates.
   * @param valueFactory The Factory to add to the repository.
   */
  public void addFactory(Class<?> clazz, ValueFactory<?> valueFactory) {
    factories.put(Objects.requireNonNull(clazz), Objects.requireNonNull(valueFactory));
  }


  /**
   * <p>
   * Add the specified FactoryLookup. The factory lookups are consulted after  will be first consulted when looking for a Factory
   */
  public void addFactoryLookup(FactoryLookup factoryLookup) {
    factoryLookups.add(Objects.requireNonNull(factoryLookup));
  }


  public ValueFactory<?> getFactory(Type type) {
    // First check the factories map
    ValueFactory<?> factory;
    if (type instanceof Class<?> clazz) {
      factory = factories.get(clazz);
      if (factory != null) {
        return factory;
      }
    }

    // Now try the lookups, most recently added first
    int p = factoryLookups.size();
    while (p-- > 0) {
      FactoryLookup lookup = factoryLookups.get(p);
      if (lookup.hasFactory(type)) {
        factory = lookup.getFactory(type);
        return factory;
      }
    }

    throw new NoSuchFactoryException("No factory found for " + type);
  }


  public void loadDefaults(TestContext context) {
    BasicFactories.load(context, this);
    FileFactories.load(context, this);
    NetFactories.load(context, this);
    TimeFactories.load(context, this);
    UtilFactories.load(context, this);


  }

}
