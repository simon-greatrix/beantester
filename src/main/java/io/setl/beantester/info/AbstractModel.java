package io.setl.beantester.info;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

/**
 * An entity that can be described by a set of properties.
 *
 * @param <M> the model type
 */
public class AbstractModel<M extends AbstractModel<M> & Model<M>> implements Model<M> {

  protected final TreeMap<String, Property> properties = new TreeMap<>();


  /** Default constructor. */
  public AbstractModel() {
    // do nothing
  }


  /**
   * Copy constructor.
   *
   * @param properties the properties to copy
   */
  protected AbstractModel(Collection<Property> properties) {
    for (Property p : properties) {
      this.properties.put(p.getName(), new Property(p));
    }
  }


  @Override
  public Collection<Property> getProperties() {
    return properties.values();
  }


  @Override
  @SuppressWarnings("unchecked")
  public M setProperties(Collection<Property> newProperties) {
    for (Property property : newProperties) {
      properties.put(property.getName(), property);
    }
    return (M) this;
  }


  @Override
  @SuppressWarnings("unchecked")
  public M setProperty(Property property) {
    properties.put(property.getName(), property);
    return (M) this;
  }


  @Override
  public Property getProperty(String name) {
    if (name != null) {
      return properties.get(name);
    }
    return null;
  }


  @Override
  public Set<String> getPropertyNames() {
    return properties.keySet();
  }


  @Override
  @SuppressWarnings("unchecked")
  public M removeProperty(String name) {
    if (name != null) {
      properties.remove(name);
    }
    return (M) this;
  }

}
