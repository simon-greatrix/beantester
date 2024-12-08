package org.meanbean.test.internal;

import java.util.List;

import org.meanbean.bean.info.PropertyInformation;

public class NoopSideEffectDetector implements SideEffectDetector {

  public static final NoopSideEffectDetector INSTANCE = new NoopSideEffectDetector();


  @Override
  public void beforeTestProperty(PropertyInformation property, EqualityTest equalityTest) {

  }


  @Override
  public void detectAfterTestProperty() {

  }


  @Override
  public List<PropertyInformation> init(Object bean, List<PropertyInformation> readableWritableProperties) {
    return readableWritableProperties;
  }

}
