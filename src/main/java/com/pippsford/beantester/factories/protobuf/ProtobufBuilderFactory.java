package com.pippsford.beantester.factories.protobuf;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import com.pippsford.beantester.AssertionException;
import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.mirror.SerializableLambdas;
import com.pippsford.beantester.mirror.SerializableLambdas.SerializableFunction0;

/** A factory for Protobuf builders. */
public class ProtobufBuilderFactory {

  private static final EnumMap<JavaType, Class<?>> JAVA_TYPES = new EnumMap<>(JavaType.class);

  static {
    JAVA_TYPES.put(JavaType.INT, Integer.class);
    JAVA_TYPES.put(JavaType.LONG, Long.class);
    JAVA_TYPES.put(JavaType.FLOAT, Float.class);
    JAVA_TYPES.put(JavaType.DOUBLE, Double.class);
    JAVA_TYPES.put(JavaType.BOOLEAN, Boolean.class);
    JAVA_TYPES.put(JavaType.STRING, String.class);
    JAVA_TYPES.put(JavaType.BYTE_STRING, ByteString.class);
  }

  private final Class<Builder> builderClass;

  private final SerializableFunction0<Builder> builderSupplier;

  private final Class<Message> messageClass;

  private final List<OneofDescriptor> oneOfFields;

  private final List<FieldDescriptor> plainFields;


  /**
   * New instance for the specified builder class.
   *
   * @param builderClass the builder class
   */
  @SuppressWarnings("unchecked")
  public ProtobufBuilderFactory(Class<Message.Builder> builderClass) {
    this.builderClass = builderClass;
    messageClass = (Class<Message>) builderClass.getEnclosingClass();
    builderSupplier = SerializableLambdas.createLambda(SerializableFunction0.class, messageClass, "newBuilder");

    Builder builder = createBuilder();
    Descriptor descriptor = builder.getDescriptorForType();
    plainFields = descriptor
        .getFields()
        .stream()
        .filter(field -> field.getContainingOneof() == null)
        .toList();
    oneOfFields = descriptor.getOneofs();
  }


  /**
   * Create a factory for the builder.
   *
   * @return the factory
   */
  public ValueFactory asBuilderFactory() {
    return new ValueFactory(
        builderClass,
        false,
        () -> build(ValueType.PRIMARY),
        () -> build(ValueType.SECONDARY),
        () -> build(ValueType.RANDOM)
    );
  }


  /**
   * Create a factory for the message that is derived from the builder.
   *
   * @return the factory
   */
  public ValueFactory asMessageFactory() {
    return new ValueFactory(
        messageClass,
        false,
        () -> build(ValueType.PRIMARY).build(),
        () -> build(ValueType.SECONDARY).build(),
        () -> build(ValueType.RANDOM).build()
    );
  }


  /**
   * Build a builder.
   *
   * @param valueType the value type
   *
   * @return the builder
   */
  public Builder build(ValueType valueType) {
    RandomGenerator random = TestContext.get().getRandom();
    Builder builder = createBuilder();

    for (FieldDescriptor field : plainFields) {
      populateField(random, field, builder, valueType);
    }

    for (OneofDescriptor oneof : oneOfFields) {
      populateOneOf(random, oneof, builder, valueType);
    }

    return builder;
  }


  private Builder createBuilder() {
    try {
      return builderSupplier.exec();
    } catch (Throwable e) {
      throw new AssertionException("Failed to create builder for " + messageClass, e);
    }
  }


  private Supplier<Object> createValueMaker(RandomGenerator random, FieldDescriptor field, Builder builder, ValueType valueType) {
    if (field.isMapField()) {
      // Map requires special handling
      return () -> {
        Builder mapBuilder = builder.newBuilderForField(field);
        for (FieldDescriptor subField : field.getMessageType().getFields()) {
          populateField(random, subField, mapBuilder, valueType);
        }
        return mapBuilder.build();
      };
    }

    // Non map fields
    JavaType javaType = field.getJavaType();
    if (javaType == JavaType.MESSAGE) {
      Class<?> fieldType = builder.newBuilderForField(field).getDefaultInstanceForType().getClass();
      return () -> TestContext.get().getFactories().getFactory(messageClass, field.getName(), fieldType).create(valueType);
    }

    if (javaType == JavaType.ENUM) {
      Optional<ValueFactory> opt = TestContext.get().getFactories().tryGetOverride(messageClass, field.getName(), Enum.class);
      if (opt.isPresent()) {
        ValueFactory vf = opt.get();
        return () -> vf.create(valueType);
      }

      return () -> randomEnum(random, field, valueType);
    }

    Class<?> fieldType = JAVA_TYPES.get(javaType);
    return () -> TestContext.get().getFactories().getFactory(messageClass, field.getName(), fieldType).create(valueType);
  }


  private void populateField(RandomGenerator random, FieldDescriptor field, Builder builder, ValueType valueType) {
    if ((field.isOptional() || field.isRepeated()) && ValueFactory.getStructureDepth() >= TestContext.get().getStructureDepth()) {
      return;
    }

    Supplier<Object> valueMaker = createValueMaker(random, field, builder, valueType);

    if (field.isRepeated()) {
      int collectionSize = switch (valueType) {
        case PRIMARY -> 1;
        case SECONDARY -> 2;
        default -> 1 + random.nextInt(4);
      };
      for (int i = 0; i < collectionSize; i++) {
        builder.addRepeatedField(field, valueMaker.get());
      }
    } else {
      builder.setField(field, valueMaker.get());
    }
  }


  private void populateOneOf(RandomGenerator random, OneofDescriptor descriptor, Builder builder, ValueType valueType) {
    int fieldCount = descriptor.getFieldCount();
    int oneofCase = switch (valueType) {
      case PRIMARY -> 0;
      case SECONDARY -> Math.min(2, fieldCount) - 1;
      default -> random.nextInt(fieldCount);
    };
    FieldDescriptor selectedCase = descriptor.getField(oneofCase);
    populateField(random, selectedCase, builder, valueType);
  }


  private EnumValueDescriptor randomEnum(RandomGenerator random, FieldDescriptor field, ValueType valueType) {
    EnumDescriptor enumType = field.getEnumType();
    List<EnumValueDescriptor> values = enumType.getValues();
    int size = values.size();
    return switch (valueType) {
      case PRIMARY -> values.get(0);
      case SECONDARY -> values.get(size > 1 ? 1 : 0);
      default -> values.get(random.nextInt(size));
    };
  }

}
