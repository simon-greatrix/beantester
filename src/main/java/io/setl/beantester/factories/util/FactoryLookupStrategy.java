package io.setl.beantester.factories.util;

import org.meanbean.bean.info.BeanInformation;
import org.meanbean.bean.info.PropertyInformation;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.NoSuchFactoryException;
import io.setl.beantester.factories.Factory;
import org.meanbean.test.BeanTestException;
import org.meanbean.test.Configuration;
import java.util.random.RandomGenerator;
import org.meanbean.util.ServiceDefinition;

/**
 * Defines a means of acquiring a Factory in the context of testing bean properties
 *
 * @author Graham Williamson
 */
public interface FactoryLookupStrategy {

  static FactoryLookupStrategy getInstance() {
    return getServiceDefinition().getServiceFactory().getFirst();
  }

  static ServiceDefinition<FactoryLookupStrategy> getServiceDefinition() {
    return new ServiceDefinition<>(
        FactoryLookupStrategy.class,
        new Class<?>[]{FactoryCollection.class, RandomGenerator.class},
        new Object[]{FactoryCollection.getInstance(), RandomGenerator.getInstance()}
    );
  }

  /**
   * <p>
   * Get a factory for the specified property that is of the specified type. <br>
   * </p>
   *
   * <p>
   * If ultimately a suitable Factory cannot be found or created, a NoSuchFactoryException detailing the problem is
   * thrown.
   * </p>
   *
   * @param beanInformation     Information about the bean the property belongs to.
   * @param propertyInformation Information about the property.
   * @param configuration       An optional Configuration object that may contain an override Factory for the specified property. Pass
   *                            <code>null</code> if no Configuration exists.
   *
   * @return A Factory that may be used to create objects appropriate for the specified property.
   *
   * @throws IllegalArgumentException If any of the required parameters are deemed illegal. For example, if any are null.
   * @throws NoSuchFactoryException   If an unexpected exception occurs when getting the Factory, including failing to find a suitable
   *                                  Factory.
   */
  Factory<?> getFactory(
      BeanInformation beanInformation, PropertyInformation propertyInformation,
      Configuration configuration
  ) throws IllegalArgumentException, BeanTestException;

}
