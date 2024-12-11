package io.setl.beantester.info;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction0;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;

/**
 * The bean holder holds a bean and manages its creation.
 */
public class BeanHolder {

  /** The current property values of the bean. */
  private final LinkedHashMap<String,Object> values = new LinkedHashMap<>();

  private SerializableFunction1<Object,Object> bean;

  private SerializableFunction0<Object> builder;

  private final BeanInformation information;

  public BeanHolder(BeanInformation information) {
    this.information = information;
  }


  public Class<?> getBeanClass() {
    return null;
  }


  public Collection<PropertyInformation> getValues() {
    return Collections.emptyList();
  }


  public Collection<String> getPropertyNames() {
    return Collections.emptyList();
  }


  public void setProperty(String propertyName, Object value) {
    synchronized (values) {
      values.remove(propertyName);
      values.put(propertyName, value);
      builder = null;
      bean = null;
    }
  }

  public Object getProperty(String propertyName) {
    synchronized (values) {
      if( bean==null ) {

      }
      return values.get(propertyName);
    }
  }
}
