package io.setl.beantester.info;

import java.util.Collection;
import java.util.Set;

/** A model is something with properties. */
public interface Model<M extends Model<M>> {

  /**
   * Get information about all properties of the bean. The returned list is immutable.
   *
   * @return A Collection of all properties of the bean.
   */
  Collection<Property> getProperties();

  /**
   * Add a collection of properties to this.
   *
   * @param properties properties to add
   *
   * @return this
   */
  M setProperties(Collection<Property> properties);


  /**
   * Add a property to this.
   *
   * @param property the property to add
   *
   * @return this
   */
  M setProperty(Property property);

  /**
   * Get information about a specific property of the bean.
   *
   * @param name The name of the property to get information about.
   *
   * @return Information about the specified property.
   */
  Property getProperty(String name);


  /** Get the names of all the properties. */
  Set<String> getPropertyNames();


  /**
   * Remove a property from this.
   *
   * @param name the name of the property to remove
   *
   * @return this
   */
  M removeProperty(String name);

}
