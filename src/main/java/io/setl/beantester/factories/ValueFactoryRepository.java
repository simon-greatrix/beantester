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
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.Property;
import io.setl.beantester.mirror.Types;

/**
 * Repository for value factories in the current test context.
 */
public class ValueFactoryRepository {

  private final HashMap<Class<?>, ValueFactory<?>> factories = new HashMap<>();

  private final List<FactoryLookup> factoryLookups = new ArrayList<>();

  private final HashMap<Class<?>, HashMap<String, ValueFactory<?>>> overrides = new HashMap<>();

  private BeanFactoryLookup beanFactoryLookup;


  /**
   * Add the specified Factory to the collection. If a Factory is already registered against the specified class, the existing registered Factory will be
   * replaced with the Factory you specify here.
   *
   * @param clazz        The type of objects the Factory creates.
   * @param valueFactory The Factory to add to the repository.
   */
  public void addFactory(Class<?> clazz, ValueFactory<?> valueFactory) {
    factories.put(Objects.requireNonNull(clazz), Objects.requireNonNull(valueFactory));
  }


  /**
   * Add the specified bean description as a factory to the collection. If a Factory is already registered against the specified class, the existing registered
   * Factory will be replaced with the one derived from the bean description you specify here.
   *
   * @param description The bean description
   */
  public void addFactory(BeanDescription description) {
    ValueFactory<?> valueFactory = description.createHolder();
    addFactory(description.beanClass(), valueFactory);
  }


  /**
   * Add the specified Factory for a specific property on a bean to the collection. If a Factory is already registered against the specified class, the existing
   * registered Factory will be
   * replaced with the Factory you specify here.
   *
   * @param clazz        The bean class.
   * @param propertyName the property name
   * @param valueFactory The Factory to add to the repository.
   */
  public void addFactory(Class<?> clazz, String propertyName, ValueFactory<?> valueFactory) {
    overrides.computeIfAbsent(clazz, k -> new HashMap<>()).put(propertyName, valueFactory);
  }


  /**
   * Add the specified FactoryLookup. The factory lookups are consulted after specific factories.
   */
  public void addFactoryLookup(FactoryLookup factoryLookup) {
    factoryLookups.add(Objects.requireNonNull(factoryLookup));
  }


  /**
   * Create a candidate value for the specified property.
   *
   * @param bean     the bean's information
   * @param property the property's information
   *
   * @return the candidate value
   */
  public Object create(ValueType type, BeanDescription bean, Property property) {
    Class<?> beanClass = bean.beanClass();
    String propertyName = property.name();
    ValueFactory<?> factory = overrides.computeIfAbsent(beanClass, k -> new HashMap<>()).get(propertyName);
    if (factory == null) {
      factory = getFactory(property.type());
    }
    return factory.create(type);
  }


  /**
   * Get a value factory of the specified type.
   *
   * @param type the type.
   *
   * @return the factory
   */
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
        if (type instanceof Class<?> clazz) {
          factories.put(clazz, factory);
        }
        return factory;
      }
    }

    // Finally, try the bean factory lookup
    if (beanFactoryLookup.hasFactory(type)) {
      factory = beanFactoryLookup.getFactory(type);
      if (type instanceof Class<?> clazz) {
        factories.put(clazz, factory);
      }
      return factory;
    }

    if (!(type instanceof Class<?>)) {
      return getFactory(Types.getRawType(type));
    }

    throw new NoSuchFactoryException("No factory found for " + type);
  }


  /**
   * Load the default factories into this repository.
   *
   * @param context the test context
   */
  public void loadDefaults(TestContext context) {
    BasicFactories.load(context, this);
    FileFactories.load(context, this);
    NetFactories.load(context, this);
    TimeFactories.load(context, this);
    UtilFactories.load(context, this);

    beanFactoryLookup = new BeanFactoryLookup(context);
  }

}
