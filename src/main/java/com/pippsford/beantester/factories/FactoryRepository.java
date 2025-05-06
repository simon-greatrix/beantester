package com.pippsford.beantester.factories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.factories.basic.BasicFactories;
import com.pippsford.beantester.factories.bean.BeanFactoryLookup;
import com.pippsford.beantester.factories.io.FileFactories;
import com.pippsford.beantester.factories.json.JsonFactories;
import com.pippsford.beantester.factories.net.NetFactories;
import com.pippsford.beantester.factories.protobuf.ProtobufMessageLookup;
import com.pippsford.beantester.factories.time.TimeFactories;
import com.pippsford.beantester.factories.util.UtilFactories;
import com.pippsford.beantester.info.BeanDescription;
import com.pippsford.beantester.info.Property;
import com.pippsford.beantester.mirror.Executables;

/**
 * Repository for value factories in the current test context.
 */
public class FactoryRepository {

  private final BeanFactoryLookup beanFactoryLookup = new BeanFactoryLookup();

  private final HashMap<Type, ValueFactory> factories = new HashMap<>();

  private final List<FactoryLookup> factoryLookups = new ArrayList<>();

  private final HashMap<Class<?>, HashMap<String, ValueFactory>> overrides = new HashMap<>();


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
    addFactory(BeanFactoryLookup.toFactory(description));
  }


  /**
   * Add the specified Factory for a specific property on a bean to the collection. If a Factory is already registered against the specified class, the existing
   * registered Factory will be
   * replaced with the Factory you specify here.
   *
   * @param beanClass    The bean class.
   * @param propertyName the property name
   * @param valueFactory The Factory to add to the repository.
   */
  public void addFactory(Class<?> beanClass, String propertyName, ValueFactory valueFactory) {
    overrides.computeIfAbsent(beanClass, k -> new HashMap<>()).put(propertyName, valueFactory);
  }


  /**
   * Add the specified FactoryLookup. The factory lookups are consulted after specific factories.
   */
  public void addFactoryLookup(FactoryLookup factoryLookup) {
    factoryLookups.add(Objects.requireNonNull(factoryLookup));
  }


  /**
   * Copy the specified factories into this repository.
   *
   * @param factories the factories to copy
   */
  public void copy(FactoryRepository factories) {
    this.factories.putAll(factories.factories);
    this.factoryLookups.addAll(factories.factoryLookups);

    for (var e : factories.overrides.entrySet()) {
      this.overrides.computeIfAbsent(e.getKey(), k -> new HashMap<>()).putAll(e.getValue());
    }
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
  public Object create(Class<?> beanClass, Property property, ValueType type) {
    ValueFactory factory = getFactory(beanClass, property.getName(), property.getType());
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
    ValueFactory factory = getFactoryInternal(type);
    if (factory == null) {
      throw new NoSuchFactoryException("No factory found for " + type);
    }
    return factory;
  }


  /**
   * Get a value factory of the specified type.
   *
   * @param beanClass    the class that has the property
   * @param propertyName the name of the property
   * @param type         the property's type.
   *
   * @return the factory
   */
  public ValueFactory getFactory(Class<?> beanClass, String propertyName, Type type) {
    Optional<ValueFactory> optionalFactory = tryGetOverride(beanClass, propertyName, type);
    return optionalFactory.orElseGet(() -> getFactory(type));
  }


  private ValueFactory getFactoryInternal(Type type) {
    // First check the factories map
    ValueFactory factory = factories.get(type);
    if (factory != null) {
      return factory;
    }

    // Now try the lookups, most recently added first
    int p = factoryLookups.size();
    while (p-- > 0) {
      FactoryLookup lookup = factoryLookups.get(p);
      Optional<ValueFactory> optionalFactory = lookup.getFactory(type);
      if (optionalFactory.isPresent()) {
        factory = optionalFactory.get();
        factories.put(type, factory);
        return factory;
      }
    }

    // Finally, try the bean factory lookup
    Optional<ValueFactory> optionalFactory = beanFactoryLookup.getFactory(type);
    if (optionalFactory.isPresent()) {
      factory = optionalFactory.get();
      factories.put(type, factory);
      return factory;
    }

    if (!(type instanceof Class<?>)) {
      return getFactory(Executables.getRawType(type));
    }

    // not found
    return null;
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
    ProtobufMessageLookup.load(this);
    JsonFactories.load(this);
  }


  /**
   * Try to get a factory for the specified type.
   *
   * @param type the type
   *
   * @return the factory, if found
   */
  public Optional<ValueFactory> tryGetFactory(Type type) {
    return Optional.ofNullable(getFactoryInternal(type));
  }


  /**
   * Try and get the factory for the specified property, if it has been overridden.
   *
   * @param beanClass    the class that has the property
   * @param propertyName the property's name
   * @param propertyType the property's type
   *
   * @return the factory, if found
   */
  public Optional<ValueFactory> tryGetOverride(Class<?> beanClass, String propertyName, Type propertyType) {
    HashMap<String, ValueFactory> map = overrides.computeIfAbsent(beanClass, k -> new HashMap<>());

    // Try known overrides
    ValueFactory factory = map.get(propertyName);
    if (factory != null) {
      return Optional.of(factory);
    }

    // Try the factory lookups
    for (FactoryLookup lookup : factoryLookups) {
      Optional<ValueFactory> optionalFactory = lookup.getFactory(beanClass, propertyName, propertyType);
      if (optionalFactory.isPresent()) {
        map.put(propertyName, optionalFactory.get());
        return optionalFactory;
      }
    }

    // No override
    return Optional.empty();
  }

}
