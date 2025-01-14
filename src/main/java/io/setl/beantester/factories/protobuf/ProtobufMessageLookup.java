package io.setl.beantester.factories.protobuf;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import io.setl.beantester.AssertionException;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.mirror.SerializableLambdas;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction0;

/**
 * A lookup for Protobuf messages and builders.
 */
public class ProtobufMessageLookup implements FactoryLookup {

  public static void load(FactoryRepository factoryRepository) {
    factoryRepository.addFactoryLookup(new ProtobufMessageLookup());
    factoryRepository.addFactory(new ByteStringValueFactory());
  }


  private final Map<Class<?>, ValueFactory> knownFactories = new HashMap<>();


  @Override
  @SuppressWarnings("unchecked")
  public Optional<ValueFactory> getFactory(Type type) {
    if (!(type instanceof Class<?> clazz)) {
      return Optional.empty();
    }

    ValueFactory factory = knownFactories.get(clazz);
    if (factory != null) {
      return Optional.of(factory);
    }

    if (Builder.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
      ProtobufBuilderFactory pbf = new ProtobufBuilderFactory((Class<Builder>) clazz);
      ValueFactory vf = pbf.asBuilderFactory();
      knownFactories.put(clazz, vf);
      return Optional.of(vf);
    }

    if (Message.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
      try {
        SerializableFunction0<?> builderSupplier = SerializableLambdas.createLambda(SerializableFunction0.class, clazz, "newBuilder");
        Class<Builder> builderClass = (Class<Builder>) builderSupplier.exec().getClass();
        ProtobufBuilderFactory pbf = new ProtobufBuilderFactory(builderClass);
        ValueFactory vf = pbf.asMessageFactory();
        knownFactories.put(clazz, vf);
        return Optional.of(vf);
      } catch (Throwable e) {
        throw new AssertionException("Failed to create a build for " + clazz, e);
      }
    }

    return Optional.empty();
  }

}
