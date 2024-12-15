package io.setl.beantester.info;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

public class AbstractModel<M extends AbstractModel<M>> implements Model<M> {

  protected final TreeMap<String, PropertyInformation> properties = new TreeMap<>();


  @Override
  public Collection<PropertyInformation> properties() {
    return properties.values();
  }


  @Override
  public M properties(Collection<PropertyInformation> newProperties) {
    for (PropertyInformation property : newProperties) {
      properties.put(property.name(), property);
    }
    return (M) this;
  }


  @Override
  public M property(PropertyInformation property) {
    properties.put(property.name(), property);
    return (M) this;
  }


  @Override
  public PropertyInformation property(String name) {
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
  public M removeProperty(String name) {
    if (name != null) {
      properties.remove(name);
    }
    return (M) this;
  }

}
