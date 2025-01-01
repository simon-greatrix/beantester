package io.setl.beantester.factories;

import java.lang.reflect.Type;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.basic.StringValueFactory;
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
    if (ByteString.class.equals(type)) {
      StringValueFactory factory = new StringValueFactory();
      return new ValueFactory(ByteString.class, v -> ByteString.copyFromUtf8(String.valueOf(factory.create(v))));
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
    if (ByteString.class.equals(type)) {
      return true;
    }

    return (type instanceof Class<?> clazz) && Message.class.isAssignableFrom(clazz);
  }

}
