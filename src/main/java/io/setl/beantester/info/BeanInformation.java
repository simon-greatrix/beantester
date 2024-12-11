package io.setl.beantester.info;

import java.util.Collection;

/**
 * Defines an object that provides information about a JavaBean.
 *
 * @author Graham Williamson
 */
public interface BeanInformation {

  Object newInstance();

  /**
   * Get the type of bean this object contains information about.
   *
   * @return The type of bean this object contains information about.
   */
  Class<?> getBeanClass();

  /**
   * Get information about all properties of the bean.
   *
   * @return A Collection of all properties of the bean.
   */
  Collection<PropertyInformation> getProperties();

  /**
   * Get the names of all properties of the bean.
   *
   * @return A Collection of names of all properties of the bean.
   */
  Collection<String> getPropertyNames();

}
