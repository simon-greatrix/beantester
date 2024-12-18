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


  @Override
  public Collection<Property> properties() {
    return properties.values();
  }


  @Override
  @SuppressWarnings("unchecked")
  public M properties(Collection<Property> newProperties) {
    for (Property property : newProperties) {
      properties.put(property.name(), property);
    }
    return (M) this;
  }


  @Override
  @SuppressWarnings("unchecked")
  public M property(Property property) {
    properties.put(property.name(), property);
    return (M) this;
  }


  @Override
  public Property property(String name) {
    if (name != null) {
      return properties.get(name);
    }
    return null;
  }


  @Override
  public Set<String> propertyNames() {
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
