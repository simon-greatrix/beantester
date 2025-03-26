package com.pippsford.beantester.info;

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

  private boolean isChanged = true;


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
      Property newProperty = new Property(p);
      this.properties.put(p.getName(), newProperty);
      newProperty.setModel(this);
    }
  }


  protected void clearChanged() {
    isChanged = false;
  }


  @Override
  public Collection<Property> getProperties() {
    return properties.values();
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
    return Set.copyOf(properties.keySet());
  }


  protected boolean isChanged() {
    return isChanged;
  }


  @Override
  public void notifyChanged(Property property) {
    isChanged = true;
  }


  @Override
  @SuppressWarnings("unchecked")
  public M removeProperty(String name) {
    isChanged = true;
    if (name != null) {
      Property p = properties.remove(name);
      if (p != null) {
        p.setModel(null);
      }
    }
    return (M) this;
  }


  @Override
  @SuppressWarnings("unchecked")
  public M setProperties(Collection<Property> newProperties) {
    isChanged = true;
    for (Property property : newProperties) {
      properties.put(property.getName(), property);
      property.setModel(this);
    }
    return (M) this;
  }


  @Override
  @SuppressWarnings("unchecked")
  public M setProperty(Property property) {
    isChanged = true;
    properties.put(property.getName(), property);
    property.setModel(this);
    return (M) this;
  }

}
