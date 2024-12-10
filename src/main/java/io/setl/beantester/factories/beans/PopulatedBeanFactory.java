package io.setl.beantester.factories.beans;

import java.util.Map;

import org.meanbean.bean.info.BeanInformation;
import org.meanbean.bean.util.BasicBeanPopulator;
import org.meanbean.bean.util.BeanPopulator;
import org.meanbean.bean.util.BeanPropertyValuesFactory;
import org.meanbean.factories.BasicNewObjectInstanceFactory;
import org.meanbean.factories.util.FactoryLookupStrategy;
import io.setl.beantester.factories.Factory;
import org.meanbean.test.Configuration;
import org.meanbean.util.ValidationHelper;

/**
 * Factory that creates object instances based on provided BeanInformation, assigning each instance different values.
 *
 * @author Graham Williamson
 */
public class PopulatedBeanFactory implements Factory<Object> {

  /** The BeanInformation that should be used to create instances of a bean. */
  private final BeanInformation beanInformation;

  /** Affords functionality to populate a bean (set its fields) with specified values. */
  private final BeanPopulator beanPopulator = new BasicBeanPopulator();

  /** Creates values that can be used to populate the properties of a Bean. */
  private final BeanPropertyValuesFactory beanPropertyValuesFactory;


  /**
   * Construct a new Factory that creates object instances based on provided BeanInformation, assigning each instance
   * different field values.
   *
   * @param beanInformation       Information used to create instances of a bean.
   * @param factoryLookupStrategy Provides a means of acquiring Factories that can be used to create values for the fields of new object
   *                              instances.
   *
   * @throws IllegalArgumentException If either the specified BeanInformation or the FactoryLookupStrategy is deemed illegal. For example,
   *                                  if either is <code>null</code>.
   */
  public PopulatedBeanFactory(BeanInformation beanInformation, FactoryLookupStrategy factoryLookupStrategy, Configuration configuration)
      throws IllegalArgumentException {
    ValidationHelper.ensureExists("beanInformation", "construct Factory", beanInformation);
    ValidationHelper.ensureExists("factoryLookupStrategy", "construct Factory", factoryLookupStrategy);
    this.beanInformation = beanInformation;
    beanPropertyValuesFactory = new BeanPropertyValuesFactory(beanInformation, factoryLookupStrategy, configuration);
  }


  /**
   * Create a new instance of the Bean described in the provided BeanInformation.
   *
   * @throws BeanCreationException If an error occurs when creating an instance of the Bean.
   */
  @Override
  public Object create() throws BeanCreationException {
    Map<String, Object> propertyValues = beanPropertyValuesFactory.create();
    Factory<Object> beanFactory = BasicNewObjectInstanceFactory.findBeanFactory(beanInformation.getBeanClass());
    Object result = beanFactory.create();
    return beanPopulator.populate(result, beanInformation, propertyValues);
  }

}
