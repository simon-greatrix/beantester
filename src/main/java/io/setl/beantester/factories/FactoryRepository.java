package io.setl.beantester.factories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.factories.basic.BasicFactories;
import io.setl.beantester.factories.io.FileFactories;
import io.setl.beantester.factories.net.NetFactories;
import io.setl.beantester.factories.time.TimeFactories;
import io.setl.beantester.factories.util.UtilFactories;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.Property;
import io.setl.beantester.mirror.Executables;

/**
 * Repository for value factories in the current test context.
 */
public class FactoryRepository {

  private final HashMap<Type, ValueFactory> factories = new HashMap<>();

  private final List<FactoryLookup> factoryLookups = new ArrayList<>();

  private final HashMap<Class<?>, HashMap<String, ValueFactory>> overrides = new HashMap<>();

  private BeanFactoryLookup beanFactoryLookup = new BeanFactoryLookup();


  /**
   * Add the specified Factory to the collection. If a Factory is already registered against the specified class, the existing registered Factory will be
   * replaced with the Factory you specify here.
   *
   * @param valueFactory The Factory to add to the repository.
   */
  public void addFactory(ValueFactory valueFactory) {
    factories.put(valueFactory.getType(), Objects.requireNonNull(valueFactory));
  }


  /**
   * Add the specified bean description as a factory to the collection. If a Factory is already registered against the specified class, the existing registered
   * Factory will be replaced with the one derived from the bean description you specify here.
   *
   * @param description The bean description
   */
  public void addFactory(BeanDescription description) {
    addFactory(description.createHolder().asFactory());
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
  public void addFactory(Class<?> clazz, String propertyName, ValueFactory valueFactory) {
    overrides.computeIfAbsent(clazz, k -> new HashMap<>()).put(propertyName, valueFactory);
  }


  /**
   * Add the specified FactoryLookup. The factory lookups are consulted after specific factories.
   */
  public void addFactoryLookup(FactoryLookup factoryLookup) {
    factoryLookups.add(Objects.requireNonNull(factoryLookup));
  }


  public void copy(FactoryRepository factories) {
    this.factories.putAll(factories.factories);
    this.factoryLookups.addAll(factories.factoryLookups);
    this.overrides.putAll(factories.overrides);
  }


  /**
   * Create a candidate value for the specified property.
   *
   * @param type      the type of value to create
   * @param beanClass the bean's class
   * @param property  the property's information
   *
   * @return the candidate value
   */
  public Object create(ValueType type, Class<?> beanClass, Property property) {
    String propertyName = property.getName();
    ValueFactory factory = overrides.computeIfAbsent(beanClass, k -> new HashMap<>()).get(propertyName);
    if (factory == null) {
      factory = getFactory(property.getType());
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
  public ValueFactory getFactory(Type type) {
    // First check the factories map
    ValueFactory factory = factories.get(type);
    if (factory != null) {
      return factory;
    }

    // Now try the lookups, most recently added first
    int p = factoryLookups.size();
    while (p-- > 0) {
      FactoryLookup lookup = factoryLookups.get(p);
      if (lookup.hasFactory(type)) {
        factory = lookup.getFactory(type);
        factories.put(type, factory);
        return factory;
      }
    }

    // Finally, try the bean factory lookup
    if (beanFactoryLookup.hasFactory(type)) {
      factory = beanFactoryLookup.getFactory(type);
      factories.put(type, factory);
      return factory;
    }

    if (!(type instanceof Class<?>)) {
      return getFactory(Executables.getRawType(type));
    }

    throw new NoSuchFactoryException("No factory found for " + type);
  }


  /**
   * Get the registered classes. Used for testing.
   *
   * @return the registered classes
   */
  Set<Type> getRegisteredClasses() {
    return factories.keySet();
  }


  /**
   * Load the default factories into this repository.
   */
  public void loadDefaults() {
    BasicFactories.load(this);
    FileFactories.load(this);
    NetFactories.load(this);
    TimeFactories.load(this);
    UtilFactories.load(this);
  }

}
