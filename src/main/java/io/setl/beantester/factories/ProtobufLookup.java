package io.setl.beantester.factories;

import java.lang.reflect.Type;

import com.google.protobuf.Message;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.Specs;

public class ProtobufLookup implements FactoryLookup {

  @Override
  public ValueFactory getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
    if (!hasFactory(type)) {
      throw new NoSuchFactoryException("Cannot create factory for: " + type);
    }
    if (!(type instanceof Class<?> clazz)) {
      throw new IllegalArgumentException("Type must be a class");
    }

    Class<? extends Message> messageType = clazz.asSubclass(Message.class);
    BeanDescription description = BeanDescription.create(
        messageType,
        Specs.builder("newBuilder", "build")
    );

    return description.createHolder().asFactory();
  }


  @Override
  public boolean hasFactory(Type type) throws IllegalArgumentException {
    return (type instanceof Class<?> clazz) && Message.class.isAssignableFrom(clazz);
  }

}
