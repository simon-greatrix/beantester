package io.setl.beantester.factories;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kohsuke.MetaInfServices;
import org.meanbean.util.Order;
import java.util.random.RandomGenerator;
import org.meanbean.util.ValidationHelper;

/**
 * Concrete collection factories of different types of objects.
 *
 * @author Graham Williamson
 */
@Order(8000)
@MetaInfServices
public final class FactoryRepository implements FactoryCollection {

  /** A Map of Factory objects */
  private final Map<Type, Factory<?>> factories = new ConcurrentHashMap<>();

  /** Random number generator used by factories to randomly generate values. */
  private final RandomGenerator randomValueGenerator = RandomGenerator.getInstance();


  public FactoryRepository() throws IllegalArgumentException {
    initialize();
  }


  /**
   * <p>
   * Add the specified Factory to the collection.
   * </p>
   *
   * <p>
   * If a Factory is already registered against the specified class, the existing registered Factory will be replaced
   * with the Factory you specify here.
   * </p>
   *
   * @param clazz   The type of objects the Factory creates. The class type will be used to generate a key with which the
   *                Factory can be retrieved from the collection at a later stage.
   * @param factory The Factory to add to the collection.
   *
   * @throws IllegalArgumentException If either of the required parameters are deemed illegal.
   */
  @Override
  public void addFactory(Class<?> clazz, Factory<?> factory) throws IllegalArgumentException {
    ValidationHelper.ensureExists("clazz", "add Factory", clazz);
    ValidationHelper.ensureExists("factory", "add Factory", factory);
    factories.put(clazz, factory);
  }


  @Override
  public void addFactoryLookup(FactoryLookup factoryLookup) {
    throw new UnsupportedOperationException();
  }


  Map<Type, Factory<?>> getFactories() {
    return factories;
  }


  /**
   * <p>
   * Get the Factory registered for the specified class.
   * </p>
   *
   * <p>
   * To check whether a Factory is registered for a specified class, please refer to
   * <code>hasFactory(Class&lt;?&gt; clazz);</code>.
   * </p>
   *
   * @param type The type Factory is registered against. This should be the type of object that the Factory
   *             creates.
   *
   * @return The requested Factory.
   *
   * @throws IllegalArgumentException If the class is deemed illegal.
   * @throws NoSuchFactoryException   If the collection does not contain a Factory registered against the specified class.
   */
  @Override
  public <T> Factory<T> getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
    ValidationHelper.ensureExists("type", "get Factory", type);
    @SuppressWarnings("unchecked")
    Factory<T> factory = (Factory<T>) factories.get(type);
    if (factory == null) {
      String message = "Failed to find a Factory registered against [" + type + "] in the Repository.";
      throw new NoSuchFactoryException(message);
    }
    return factory;
  }


  /**
   * Get a RandomNumberGenerator.
   *
   * @return A RandomNumberGenerator.
   */
  public RandomGenerator getRandomGenerator() {
    return randomValueGenerator;
  }


  /**
   * Does the collection contain a Factory registered against the specified class?
   *
   * @param type The type a Factory could be registered against. This should be the type of object that the Factory
   *             creates.
   *
   * @return <code>true</code> if the collection contains a Factory registered for the specified class;
   *     <code>false</code> otherwise.
   *
   * @throws IllegalArgumentException If the clazz is deemed illegal.
   */
  @Override
  public boolean hasFactory(Type type) throws IllegalArgumentException {
    ValidationHelper.ensureExists("type", "check collection for Factory", type);
    return factories.containsKey(type);
  }


  /**
   * Initialize the repository prior to public use.
   */
  private void initialize() {
    FactoryCollectionPlugin.getInstances()
        .forEach(plugin -> plugin.initialize(this, randomValueGenerator));
  }

}
