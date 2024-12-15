package io.setl.beantester.info;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.setl.beantester.mirror.Executables;
import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;

public class PropertyInformation {

  static boolean inferNullable(Method readMethod, Method writeMethod) {
    List<Annotation> names = new ArrayList<>();
    if (readMethod != null) {
      // Look at annotations on the return value
      names.addAll(Arrays.asList(readMethod.getAnnotations()));
    }
    if (writeMethod != null) {
      // Look at annotations on the first parameter.
      names.addAll(Arrays.asList(writeMethod.getParameterAnnotations()[0]));
    }

    // Check all the annotations
    for (Annotation a : names) {
      String simpleName = a.annotationType().getSimpleName();
      if (simpleName.equalsIgnoreCase("NotNull") || simpleName.equalsIgnoreCase("NonNull")) {
        return false;
      }
    }

    // default to nullable
    return true;
  }


  static void merge(Map<String, PropertyInformation> map, PropertyInformation update) {
    map.compute(update.name(), (k, v) -> {
      if (v == null) {
        return update;
      }
      return new PropertyInformation(v, update);
    });
  }


  private final String name;

  private boolean ignored = false;

  private boolean nullable = true;

  private SerializableFunction1<Object, Object> reader;

  private boolean significant = true;

  private Type type;

  private SerializableFunction2<Object, Object, Object> writer1;

  private SerializableConsumer2<Object, Object> writer2;


  /**
   * Create a copy of a property information instance.
   *
   * @param original the original instance
   */
  public PropertyInformation(PropertyInformation original) {
    this.name = original.name;
    this.ignored = original.ignored;
    this.nullable = original.nullable;
    this.reader = original.reader;
    this.significant = original.significant;
    this.type = original.type;
    this.writer1 = original.writer1;
    this.writer2 = original.writer2;
  }


  /**
   * Merge two property information instances. The update instance takes precedence.
   *
   * @param original the original instance
   * @param update   the instance to update with
   */
  public PropertyInformation(PropertyInformation original, PropertyInformation update) {
    if (!original.name.equals(update.name)) {
      throw new IllegalArgumentException("Names must match");
    }
    this.name = original.name;

    this.reader = update.reader != null ? update.reader : original.reader;
    this.type = update.type != null ? update.type : original.type;
    if (update.writer1 != null || update.writer2 != null) {
      this.writer1 = update.writer1;
      this.writer2 = update.writer2;
    } else {
      this.writer1 = original.writer1;
      this.writer2 = original.writer2;
    }

    this.ignored = update.ignored || original.ignored;
    this.significant = original.significant || update.significant;

    // We care more about nullable being false than true, so we 'and' the values.
    this.nullable = original.nullable && update.nullable;
  }


  /**
   * New instance. The new instance is nullable, significant and not ignored. It is not buildable, readable or writable.
   *
   * @param name the property name
   */
  public PropertyInformation(
      String name
  ) {
    this.name = Objects.requireNonNull(name);
  }


  /**
   * Copy all the settings from another property information instance.
   *
   * @param original the original instance
   *
   * @return this
   */
  public PropertyInformation copyFrom(PropertyInformation original) {
    this.ignored = original.ignored;
    this.nullable = original.nullable;
    this.reader = original.reader;
    this.significant = original.significant;
    this.type = original.type;
    this.writer1 = original.writer1;
    this.writer2 = original.writer2;
    return this;
  }


  /**
   * Set whether this property is completely ignored during testing.
   *
   * @param ignored true if ignored, false otherwise
   *
   * @return this
   */
  public PropertyInformation ignored(boolean ignored) {
    this.ignored = ignored;
    return this;
  }


  /**
   * Is this property completely ignored during testing?
   *
   * @return true if ignored, false otherwise
   */
  public boolean ignored() {
    return ignored;
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
   * Can this property be null?
   *
   * @param nullable true if the property can be null, false otherwise
   *
   * @return this
   */
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


  /**
   * Read the value of the property from a bean.
   *
   * @param bean the bean to read from
   *
   * @return the value of the property
   */
  public Object read(Object bean) {
    try {
      return reader != null ? reader.exec(bean) : null;
    } catch (Throwable e) {
      throw new IllegalStateException("Failed to read property " + name, e);
    }
  }


  /**
   * Can this property be read?
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
  public PropertyInformation reader(SerializableFunction1<Object, Object> reader) {
    this.reader = reader;
    return this;
  }


  /**
   * Is this property significant for equals and hash-code testing?.
   *
   * @param significant true if significant, false otherwise
   *
   * @return this
   */
  public PropertyInformation significant(boolean significant) {
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
  public PropertyInformation type(Type type) {
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
    if (writer1 != null) {
      return Executables.findGetter(writer1).getGenericParameterTypes()[0];
    }
    if (writer2 != null) {
      return Executables.findMethod(writer2).getGenericParameterTypes()[0];
    }
    if (reader != null) {
      return Executables.findGetter(reader).getGenericReturnType();
    }
    return null;
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

  public PropertyInformation writer(SerializableConsumer2<Object, Object> writer) {
    this.writer1 = null;
    this.writer2 = writer;
    return this;
  }

  public PropertyInformation writer(SerializableFunction2<Object, Object, Object> writer) {
    this.writer1 = writer;
    this.writer2 = null;
    return this;
  }

}
