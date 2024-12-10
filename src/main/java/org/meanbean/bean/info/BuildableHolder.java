package org.meanbean.bean.info;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class BuildableHolder implements BeanInformation {

  /** The current property values of the bean. */
  private final LinkedHashMap<String,Object> properties = new LinkedHashMap<>();

  private Object bean;

  private Object builder;

  private final BuildableBeanInformation information;

  public BuildableHolder(BuildableBeanInformation information) {
    this.information = information;
  }


  @Override
  public Class<?> getBeanClass() {
    return information.getBeanClass();
  }


  @Override
  public Collection<PropertyInformation> getProperties() {
    return Collections.emptyList();
  }


  @Override
  public Collection<String> getPropertyNames() {
    return Collections.emptyList();
  }


  public void setProperty(String propertyName, Object value) {
    synchronized (properties) {
      properties.remove(propertyName);
      properties.put(propertyName, value);
      builder = null;
      bean = null;
    }
  }

  public Object getProperty(String propertyName) {
    synchronized (properties) {
      if( bean==null ) {

      }
      return properties.get(propertyName);
    }
  }
}
