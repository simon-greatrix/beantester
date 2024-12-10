package io.setl.beantester.factories;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import org.kohsuke.MetaInfServices;
import org.meanbean.util.Order;
import org.meanbean.util.ServiceDefinition;
import org.meanbean.util.ServiceFactory;

/**
 * Mutable FactoryCollection that delegates to other FactoryCollection implementations
 */
@MetaInfServices
@Order(1000)
public class FactoryCollectionRepository implements FactoryCollection {

  private final List<FactoryLookup> factoryLookups = new CopyOnWriteArrayList<>();


  @Override
  public void addFactory(Class<?> clazz, Factory<?> factory) throws IllegalArgumentException {
    factoryCollections().forEach(factoryCollection -> factoryCollection.addFactory(clazz, factory));
  }


  @Override
  public void addFactoryLookup(FactoryLookup factoryLookup) {
    factoryLookups.add(0, factoryLookup);
  }


  private Stream<FactoryCollection> factoryCollections() {
    return servicesFrom(FactoryCollection.getServiceDefinition());
  }


  Stream<FactoryLookup> factoryLookups() {
    Stream<FactoryLookup> lookups = servicesFrom(FactoryLookup.getServiceDefinition());
    Stream<FactoryLookup> services = Stream.concat(lookups, factoryCollections())
        .sorted(ServiceFactory.getComparator());
    return Stream.concat(factoryLookups.stream(), services);
  }


  @Override
  public <T> Factory<T> getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
    Optional<Factory<T>> resultOptional = factoryLookups()
        .filter(factoryCollection -> factoryCollection.hasFactory(type))
        .findFirst()
        .map(factoryCollection -> factoryCollection.getFactory(type));

    if (resultOptional.isPresent()) {
      return resultOptional.get();
    }

    throw new NoSuchFactoryException("No factory found for " + type);
  }


  @Override
  public boolean hasFactory(Type type) throws IllegalArgumentException {
    return factoryLookups().anyMatch(factoryCollection -> factoryCollection.hasFactory(type));
  }


  private <T> Stream<T> servicesFrom(ServiceDefinition<T> definition) {
    return definition.getServiceFactory()
        .getAll()
        .stream()
        .filter(factoryCollection -> factoryCollection != this);
  }

}
