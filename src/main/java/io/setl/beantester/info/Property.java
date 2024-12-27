package io.setl.beantester.info;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.setl.beantester.NullBehaviour;
import io.setl.beantester.mirror.Executables;
import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;

/**
 * Description of the properties of a bean.
 */
public class Property {


  private static <T> T coalesce(T a, T b) {
    return a != null ? a : b;
  }


  static void merge(Map<String, Property> map, Property update) {
    map.compute(update.name(), (k, v) -> {
      if (v == null) {
        return update;
      }
      return new Property(v, update);
    });
  }


  /** Name of this property. */
  private final String name;

  /** Is this property completely ignored during testing? */
  private boolean ignored = false;

  /** Inferred type of this property. Can be overridden by an explicit type. */
  private Type inferredType = null;

  /** Can this property be null? */
  private boolean notNull = false;

  /** Behaviour when the property is set to null. */
  private NullBehaviour nullBehaviour;

  /** Value if the property is set to null. Requires null-behaviour of "VALUE". */
  private Object nullValue;

  /** Behaviour when the property is omitted. */
  private NullBehaviour omittedBehaviour;

  /** Value if the property is set omitted. Requires omitted-behaviour of "VALUE", and a builder pattern. */
  private Object omittedValue;

  /** Read function for this property. */
  private SerializableFunction1<?, ?> reader;

  /** Is this property significant for equals and hash-code testing? */
  private boolean significant = true;

  /** Explicit type of this property (null if not set). */
  private Type type;

  /** Write function for this property. A writer that returns a value. */
  private SerializableFunction2<Object, Object, Object> writer1;

  /** Write function for this property. A writer with a void return. */
  private SerializableConsumer2<Object, Object> writer2;


  /**
   * Create a new instance of a property information instance.
   *
   * @param name     the name of the new property
   * @param original the original instance
   */
  public Property(String name, Property original) {
    this.ignored = original.ignored;
    this.inferredType = original.inferredType;
    this.name = Objects.requireNonNull(name);
    this.notNull = original.notNull;
    this.nullBehaviour = original.nullBehaviour;
    this.nullValue = original.nullValue;
    this.omittedBehaviour = original.omittedBehaviour;
    this.omittedValue = original.omittedValue;
    this.reader = original.reader;
    this.significant = original.significant;
    this.type = original.type;
    this.writer1 = original.writer1;
    this.writer2 = original.writer2;
  }


  /**
   * Create a copy of a property information instance.
   *
   * @param original the original instance
   */
  Property(Property original) {
    this(original.name, original);
  }


  /**
   * Merge two property information instances. The update instance takes precedence.
   *
   * @param original the original instance
   * @param update   the instance to update with
   */
  private Property(Property original, Property update) {
    if (!original.name.equals(update.name)) {
      throw new IllegalArgumentException("Names must match");
    }
    this.name = original.name;

    this.reader = coalesce(update.reader, original.reader);
    this.type = coalesce(update.type, original.type);
    if (update.writer1 != null || update.writer2 != null) {
      this.writer1 = update.writer1;
      this.writer2 = update.writer2;
    } else {
      this.writer1 = original.writer1;
      this.writer2 = original.writer2;
    }

    this.ignored = update.ignored || original.ignored;
    this.notNull = original.notNull || update.notNull;
    this.significant = original.significant || update.significant;
  }


  /**
   * New instance. The new instance is nullable, significant and not ignored. It is not buildable, readable or writable.
   *
   * @param name the property name
   */
  public Property(
      String name
  ) {
    this.name = Objects.requireNonNull(name);
  }


  /**
   * Set whether this property is completely ignored during testing.
   *
   * @param ignored true if ignored, false otherwise
   *
   * @return this
   */
  public Property ignored(boolean ignored) {
    this.ignored = ignored;
    return this;
  }


  /**
   * Is this property completely ignored during testing? If a property is ignored, though if also not-null it may be set during bean creation.
   *
   * @return true if ignored, false otherwise
   */
  public boolean ignored() {
    return ignored;
  }


  private Type inferredType() {
    if (inferredType == null) {
      if (writer1 != null) {
        inferredType = Executables.findGetter(writer1).getGenericParameterTypes()[0];
      } else if (writer2 != null) {
        inferredType = Executables.findMethod(writer2).getGenericParameterTypes()[0];
      } else if (reader != null) {
        inferredType = Executables.findGetter(reader).getGenericReturnType();
      }
    }
    return inferredType;
  }


  /**
   * Get the name of this property.
   *
   * @return the name of this property
   */
  public String name() {
    return name;
  }


  /**
   * Can this property be null? If not set explicitly, this will be inferred from the annotations on the read and write methods.
   *
   * @param notNull true if the property cannot be null, false otherwise
   *
   * @return this
   */
  public Property notNull(boolean notNull) {
    this.notNull = notNull;
    return this;
  }


  /**
   * Can this property be null?.
   *
   * @return true if the property cannot be null, false otherwise
   */
  public boolean notNull() {
    return notNull;
  }


  /**
   * How does the property behave when set to null?.
   *
   * @return the behaviour
   */
  public NullBehaviour nullBehaviour() {
    return nullBehaviour;
  }


  /**
   * Set the null-behaviour for this property.
   *
   * @param nullBehaviour the new null-behaviour
   *
   * @return this
   */
  public Property nullBehaviour(NullBehaviour nullBehaviour) {
    this.nullBehaviour = nullBehaviour;
    return this;
  }


  /**
   * What value should be used when the property is set to null?.
   *
   * @return the value - note that the value cannot be null, so null indicates unknown.
   */
  public Object nullValue() {
    return nullValue;
  }


  /**
   * What value should be used when the property is set to null?.
   *
   * @param nullValue the non-null value to use
   *
   * @return this;
   */
  public Property nullValue(Object nullValue) {
    this.nullValue = Objects.requireNonNull(nullValue);
    return this;
  }


  /**
   * What is the behaviour when the property is omitted on a builder?.
   *
   * @return the behaviour when the property is omitted
   */
  public NullBehaviour omittedBehaviour() {
    return omittedBehaviour;
  }


  public Property omittedBehaviour(NullBehaviour omittedBehaviour) {
    this.omittedBehaviour = omittedBehaviour;
    return this;
  }


  public Object omittedValue() {
    return omittedValue;
  }


  public Property omittedValue(Object omittedValue) {
    this.omittedValue = omittedValue;
    return this;
  }


  /**
   * Read the value of the property from a bean.
   *
   * @param bean the bean to read from
   *
   * @return the value of the property
   */
  public <T, R> R read(T bean) {
    try {
      @SuppressWarnings("unchecked")
      SerializableFunction1<T, R> castReader = (SerializableFunction1<T, R>) this.reader;
      return castReader != null ? castReader.exec(bean) : null;
    } catch (Throwable e) {
      throw new IllegalStateException("Failed to read property " + name, e);
    }
  }


  /**
   * Get the read method if it exists.
   *
   * @return the read method if it exists
   */
  public Optional<Method> readMethod() {
    return Optional.ofNullable(reader).map(Executables::findGetter);
  }


  /**
   * Can this property be read? To be readable a property must have a reader function.
   *
   * @return true if the property can be read, false otherwise
   */
  public boolean readable() {
    return reader != null;
  }


  /**
   * Set the reader function for this property.
   *
   * @param reader the reader function
   *
   * @return this
   */
  public <T, R> Property reader(SerializableFunction1<T, R> reader) {
    this.reader = reader;
    inferredType = null;
    return this;
  }


  /**
   * Is this property significant for equals and hash-code testing?.
   *
   * @param significant true if significant, false otherwise
   *
   * @return this
   */
  public Property significant(boolean significant) {
    this.significant = significant;
    return this;
  }


  /**
   * Is this property significant for equals and hash-code testing?.
   *
   * @return true if the property is significant, false otherwise
   */
  public boolean significant() {
    return significant;
  }


  /**
   * Is this property testable? To be testable a property must be readable and writable.
   *
   * @return true if the property is testable, false otherwise
   */
  public boolean testable() {
    return readable() && writable();
  }


  /**
   * Set the type of this property. This will be inferred if not set explicitly.
   *
   * @param type the type of this property
   *
   * @return this
   */
  public Property type(Type type) {
    this.type = type;
    return this;
  }


  /**
   * Get the type of this property. If not set explicitly it will be inferred first from the writer's parameter type if available, then by the reader's return
   * type if that is available.
   */
  public Type type() {
    if (type != null) {
      return type;
    }
    return inferredType();
  }


  public boolean writable() {
    return writer1 != null || writer2 != null;
  }


  /**
   * Write a value to the bean.
   *
   * @param bean  the bean to write to
   * @param value the value to write
   */
  public void write(Object bean, Object value) {
    try {
      if (writer1 != null) {
        writer1.exec(bean, value);
      } else if (writer2 != null) {
        writer2.exec(bean, value);
      }
    } catch (Throwable e) {
      throw new IllegalStateException("Failed to write property " + name, e);
    }
  }


  /**
   * Get the write method, if it exists.
   *
   * @return the write method, if it exists
   */
  public Optional<Method> writeMethod() {
    if (writer1 != null) {
      Method m = Executables.findGetter(writer1);
      return Optional.of(m);
    }
    if (writer2 != null) {
      Method m = Executables.findMethod(writer2);
      return Optional.of(m);
    }
    return Optional.empty();
  }


  /**
   * Set the writer function for this property. The writer returns void.
   *
   * @param writer the new writer
   *
   * @return this
   */
  public Property writer(SerializableConsumer2<Object, Object> writer) {
    this.writer1 = null;
    this.writer2 = writer;
    inferredType = null;
    return this;
  }


  /**
   * Set the writer function for this property. The writer returns something, which will be ignored. This is for writers that support chaining or return the
   * old value of a property.
   *
   * @param writer the new writer function
   */
  public Property writer(SerializableFunction2<Object, Object, Object> writer) {
    this.writer1 = writer;
    this.writer2 = null;
    inferredType = null;
    return this;
  }

}
