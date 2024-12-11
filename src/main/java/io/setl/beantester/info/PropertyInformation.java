package io.setl.beantester.info;

import java.lang.reflect.Type;

import io.setl.beantester.mirror.Executables;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;

public class PropertyInformation {

  private final String name;

  private SerializableFunction2<Object, Object, Object> builder;

  private boolean ignored = false;

  private boolean nullable = true;

  private SerializableFunction1<Object, Object> reader;

  private boolean significant = false;

  private Type type;

  private SerializableFunction2<Object, Object, Object> writer;


  public PropertyInformation(
      String name
  ) {
    this.name = name;
  }


  public boolean buildable() {
    return builder != null;
  }


  public PropertyInformation builder(SerializableFunction2<Object, Object, Object> builder) {
    this.builder = builder;
    return this;
  }


  /**
   * The builder function for this property. This function is passed the instance of the builder and the new value and should update the builder appropriately.
   * It is expected to return the updated builder, or the old value of the property.
   *
   * @return the builder function
   */
  public SerializableFunction2<Object, Object, Object> builder() {
    return builder;
  }


  /**
   * Get the name of this property.
   *
   * @return the name of this property
   */
  public String getName() {
    return name;
  }


  public PropertyInformation ignored(boolean ignored) {
    this.ignored = ignored;
    return this;
  }


  public boolean ignored() {
    return ignored;
  }


  public PropertyInformation nullable(boolean nullable) {
    this.nullable = nullable;
    return this;
  }


  /**
   * Can this property be null?.
   *
   * @return true if the property can be null, false otherwise
   */
  public boolean nullable() {
    return nullable;
  }


  public boolean readable() {
    return reader != null;
  }


  public SerializableFunction1<Object, Object> reader() {
    return reader;
  }


  public PropertyInformation reader(SerializableFunction1<Object, Object> reader) {
    this.reader = reader;
    return this;
  }


  public PropertyInformation significant(boolean significant) {
    this.significant = significant;
    return this;
  }


  /**
   * Is this property significant for equality testing?.
   *
   * @return true if the property is significant, false otherwise
   */
  public boolean significant() {
    return significant;
  }


  /**
   * Is this property testable? To be testable a property must be readable and either writable or buildable.
   *
   * @return true if the property is testable, false otherwise
   */
  public boolean testable() {
    return readable() && (writable() || buildable());
  }


  public PropertyInformation type(Type type) {
    this.type = type;
    return this;
  }


  public Type type() {
    if (type != null) {
      return type;
    }
    if (writer != null) {
      return Executables.findGetter(writer).getGenericParameterTypes()[0];
    }
    if (reader != null) {
      return Executables.findGetter(reader).getGenericReturnType();
    }
    return null;
  }


  public boolean writable() {
    return writer != null;
  }


  public PropertyInformation writer(SerializableFunction2<Object, Object, Object> writer) {
    this.writer = writer;
    return this;
  }


  /**
   * Get the writer function for this property.
   *
   * <p>The function is given the current bean and the value and is expected to return the updated bean.</p>
   */
  public SerializableFunction2<Object, Object, Object> writer() {
    return writer;
  }

}
