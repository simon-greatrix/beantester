package org.meanbean.test.internal;

import java.util.List;

import org.meanbean.bean.info.PropertyInformation;

public interface SideEffectDetectorFactory {

  void beforeTestProperty(PropertyInformation property, EqualityTest equalityTest);

  void detectAfterTestProperty();

  List<PropertyInformation> init(Object bean, List<PropertyInformation> readableWritableProperties);

}
