package io.setl.beantester.info.specs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;

import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanDescriptionLookup;
import io.setl.beantester.info.Specs.Spec;

public class ProtobufDescriptionLookup implements BeanDescriptionLookup {

  private static Descriptor getDescriptor(Class<?> type) {
    Method method;
    try {
      method = type.getMethod("getDescriptor");
    } catch (NoSuchMethodException e) {
      return null;
    }

    if (!Descriptor.class.isAssignableFrom(method.getReturnType())) {
      return null;
    }

    int modifiers = method.getModifiers();
    if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && method.getParameterCount() == 0) {
      try {
        return (Descriptor) method.invoke(null);
      } catch (Exception e) {
        // Can't invoke it, so return null
        return null;
      }
    }

    // Can't invoke it, so return null
    return null;
  }


  @Override
  public BeanDescription getDescription(Class<?> type, Spec... specs) {
    if (!hasDescription(type, specs)) {
      throw new IllegalArgumentException("Cannot create description for: " + type);
    }

    Descriptor descriptor = getDescriptor(type);
    if (descriptor == null) {
      throw new IllegalArgumentException("No Protobuf description for: " + type);
    }

    return null;
  }


  @Override
  public boolean hasDescription(Class<?> type, Spec... specs) {
    return Message.class.isAssignableFrom(type) && getDescriptor(type) != null;
  }

}
