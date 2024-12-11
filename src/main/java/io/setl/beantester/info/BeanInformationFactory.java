package io.setl.beantester.info;


/**
 * Defines an object that creates BeanInformation objects.
 *
 * @author Graham Williamson
 */
public interface BeanInformationFactory {

  /**
   * Create a BeanInformation object from/based on the specified beanClass.
   *
   * @param beanClass The type of the object the BeanInformation information should be about.
   *
   * @return Information about the specified type, encapsulated in a BeanInformation object.
   *
   * @throws IllegalArgumentException If the beanClass is deemed illegal. For example, if it is null.
   */
  BeanInformation create(Class<?> beanClass) throws IllegalArgumentException;

}
