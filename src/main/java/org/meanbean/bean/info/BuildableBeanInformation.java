package org.meanbean.bean.info;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BuildableBeanInformation implements BeanInformation {

  private static List<PropertyInformation> findLombokProperties(Class<?> beanClass) {
    // TODO
    return new ArrayList<>();
  }


  private static <X> Constructor<X> findNoArgsConstructor(Class<X> beanClass) {
    try {
      return beanClass.getConstructor();
    } catch (NoSuchMethodException e) {
      return null;
    }
  }


  private final Class<?> beanClass;

  private final Constructor<?> constructor;

  private final List<String> names;

  private final List<PropertyInformation> properties;


  public BuildableBeanInformation(Class<?> beanClass) {
    this.beanClass = beanClass;
    this.constructor = findNoArgsConstructor(beanClass);
    properties = findLombokProperties(beanClass);
    ArrayList<String> list = new ArrayList<>();
    for (PropertyInformation property : properties) {
      list.add(property.getName());
    }
    names = Collections.unmodifiableList(list);
  }


  @Override
  public Class<?> getBeanClass() {
    return beanClass;
  }


  @Override
  public Collection<PropertyInformation> getProperties() {
    return properties;
  }


  @Override
  public Collection<String> getPropertyNames() {
    return names;
  }

}
