package io.setl.beantester.factories;

import java.util.List;

import java.util.random.RandomGenerator;
import org.meanbean.util.ServiceDefinition;

/**
 * Defines a plugin that will register Factories with the specified FactoryCollection.
 *
 * @author Graham Williamson
 */
public interface FactoryCollectionPlugin {

  static List<FactoryCollectionPlugin> getInstances() {
    return getServiceDefinition().getServiceFactory()
        .getAll();
  }

  static ServiceDefinition<FactoryCollectionPlugin> getServiceDefinition() {
    return new ServiceDefinition<>(FactoryCollectionPlugin.class);
  }

  /**
   * Initialize the plugin, adding Factories to the FactoryCollection.
   *
   * @param factoryCollection    A FactoryCollection that Factory objects can be added to.
   * @param randomValueGenerator RandomGenerator that can be used by Factory objects.
   */
  void initialize(FactoryCollection factoryCollection, RandomGenerator randomValueGenerator);

}
