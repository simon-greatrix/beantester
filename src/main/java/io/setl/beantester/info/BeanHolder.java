package io.setl.beantester.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

import io.setl.beantester.AssertionException;
import io.setl.beantester.NullBehaviour;
import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.factories.BeanFactoryLookup;
import io.setl.beantester.factories.FactoryRepository;

/**
 * The bean holder holds a bean and manages its creation.
 */
public class BeanHolder {

  private record CreatorData(LinkedHashMap<String, Object> params, HashSet<String> keys) {

  }



  @Getter
  private final BeanDescription description;

  private final HashMap<String, Object> initialValues = new HashMap<>();

  /** The property values (this may be inconsistent with the bean). */
  private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

  /** The current bean. */
  private Object bean;

  /** Creator data used in creating the current bean. */
  private CreatorData creatorData;

  /** Whether to prefer writers to creators. */
  @Getter
  @Setter
  private boolean preferWriters = TestContext.get().isPreferWriters();


  /**
   * New instance.
   *
   * @param description the description of the bean
   */
  public BeanHolder(BeanDescription description) {
    this.description = description;
    resetInitialValues();
  }


  private BeanHolder(BeanHolder copy) {
    this.description = copy.description;

    this.initialValues.putAll(copy.initialValues);
    this.values.putAll(copy.values);
    this.preferWriters = copy.preferWriters;

    this.bean = null;
    this.creatorData = null;
  }


  /**
   * Convert this to a value factory.
   *
   * @return the value factory
   */
  public ValueFactory asFactory() {
    return BeanFactoryLookup.toFactory(description);
  }


  private void buildBean() {
    setCreatorData();

    if (bean == null || !creatorData.keys.isEmpty()) {
      try {
        bean = description.getBeanCreator().apply(creatorData.params);
      } catch (Throwable e) {
        throw new IllegalStateException("Failed to create bean of type " + description.getBeanClass(), e);
      }
    }

    for (var entry : values.entrySet()) {
      String name = entry.getKey();
      if (creatorData.keys.contains(name)) {
        continue;
      }

      Object value = entry.getValue();
      Property info = this.description.getProperty(name);

      if (info != null && info.isWritable()) {
        info.write(bean, value);
      }
    }

  }


  public BeanHolder copy() {
    return new BeanHolder(this);
  }


  public Object create(ValueType type) {
    setAllProperties(type);
    return newBean();
  }


  /**
   * Create a builder with the current property values. If the bean does not user a builder, returns an empty Optional.
   *
   * @return the created builder
   */
  public Optional<Object> createBuilder() {
    if (description.getBeanCreator() instanceof BeanBuilder builder) {
      setCreatorData();
      try {
        return Optional.of(builder.build(creatorData.params));
      } catch (Throwable e) {
        throw new AssertionException("Failed to create builder for class " + description.getBeanClass(), e);
      }
    }

    return Optional.empty();
  }


  /**
   * Create a value for a named property.
   *
   * @param type the value type
   * @param name the property name
   *
   * @return the created value
   */
  public Object createValue(ValueType type, String name) {
    Property info = this.description.getBeanCreator().getProperty(name);
    if (info == null) {
      info = this.description.getProperty(name);
    }
    if (info == null) {
      throw new IllegalArgumentException("Class " + getBeanClass() + " : No property named " + name);
    }
    return TestContext.get().getFactories().create(type, this.description.getBeanClass(), info);
  }


  public Class<?> getBeanClass() {
    return description.getBeanClass();
  }


  /**
   * Get all the non-ignored writable property names.
   *
   * @return the property names
   */
  public Collection<String> getPropertyNames() {
    TreeSet<String> names = new TreeSet<>();
    description.getProperties().stream()
        .filter(i -> !i.isIgnored())
        .filter(Property::isWritable)
        .forEach(p -> names.add(p.getName()));
    description.getBeanCreator().getProperties().stream()
        .filter(i -> !i.isIgnored())
        .filter(Property::isWritable)
        .forEach(p -> names.add(p.getName()));
    return names;
  }


  /**
   * Is there an expected value? Useful for testing nulls.
   *
   * @param name the property name
   *
   * @return true if there is an expected value, false otherwise
   */
  public boolean hasExpected(String name) {
    return values.containsKey(name) || initialValues.containsKey(name);
  }


  /**
   * Is a property nullable? A property can only be nullable if both the creator and the bean consider it to be nullable.
   *
   * @param name the property name
   *
   * @return true if the property is nullable, false otherwise
   */
  public boolean isNullable(String name) {
    Property info = description.getProperty(name);

    //The property is only nullable if it is nullable everywhere.
    if (info != null && info.isNotNull()) {
      return false;
    }
    info = description.getBeanCreator().getProperty(name);
    return info == null || !info.isNotNull();
  }


  /**
   * Is a property readable? A property can be readable only in the bean.
   *
   * @param name the property name
   *
   * @return true if the property is readable, false otherwise
   */
  public boolean isReadable(String name) {
    Property info = this.description.getProperty(name);
    return info != null && info.isReadable();
  }


  /**
   * Is the property significant for equality testing? This must be set explicitly.
   *
   * @param name the property name
   *
   * @return true if the property is significant, false otherwise
   */
  public boolean isSignificant(String name) {
    Property info = this.description.getProperty(name);
    if (info != null && info.isSignificant() && info.isWritable()) {
      return true;
    }
    info = this.description.getBeanCreator().getProperty(name);
    return info != null && info.isSignificant() && info.isWritable();
  }


  /**
   * Is the property testable? A testable property is readable and writable.
   *
   * @param name the property name
   *
   * @return true if the property is testable, false otherwise
   */
  public boolean isTestable(String name) {
    boolean isIgnored = false;
    boolean isWritable = false;
    boolean isReadable = false;

    Property info = this.description.getProperty(name);
    if (info != null) {
      isIgnored = info.isIgnored();
      isWritable = info.isWritable();
      isReadable = info.isReadable();
    }

    info = this.description.getBeanCreator().getProperty(name);
    if (info != null) {
      isIgnored = isIgnored || info.isIgnored();
      isWritable = isWritable || info.isWritable();
    }

    return isWritable && isReadable && !isIgnored;
  }


  /**
   * Is a property writable? A property can be writable either in the bean or in the creator.
   *
   * @param name the property name
   *
   * @return true if the property is writable, false otherwise
   */
  public boolean isWritable(String name) {
    Property info = this.description.getProperty(name);
    if (info != null && info.isWritable()) {
      return true;
    }
    info = this.description.getBeanCreator().getProperty(name);
    return info != null && info.isWritable();
  }


  /**
   * Get the bean. Every call to this creates a new instance.
   *
   * @return the bean instance
   */
  public Object newBean() {
    bean = null;
    buildBean();
    return bean;
  }


  /**
   * Read the actual value of a property. Note that this may trigger bean creation.
   *
   * @param name the property name
   *
   * @return the actual value
   */
  public Object readActual(String name) {
    buildBean();
    Property property = description.getProperty(name);
    if (property == null) {
      throw new IllegalArgumentException("Class " + getBeanClass() + " : No property named " + name);
    }
    return property.read(bean);
  }


  /**
   * Read the expected value of a property.
   *
   * @param name the property name
   *
   * @return the expected value (null can indicate an expected null or no value)
   */
  public Object readExpected(String name) {
    Object o = values.get(name);
    if (o == null && !values.containsKey(name)) {
      o = initialValues.get(name);
    }
    return o;
  }


  /** Reset all the property values. */
  public BeanHolder reset() {
    bean = null;
    values.clear();
    resetInitialValues();
    return this;
  }


  private void resetInitialValues() {
    FactoryRepository vfr = TestContext.get().getFactories();

    // Set a value for all non-null values (including ignored ones)
    for (Property property : description.getBeanCreator().getProperties()) {
      if (property.isNotNull()) {
        initialValues.put(property.getName(), vfr.create(ValueType.PRIMARY, getBeanClass(), property));
      }
    }

    for (Property property : description.getProperties()) {
      if (property.isNotNull()) {
        initialValues.put(property.getName(), vfr.create(ValueType.PRIMARY, getBeanClass(), property));
      }
    }
  }


  /**
   * Set all properties to the same type of value.
   *
   * @param type the value type
   */
  public BeanHolder setAllProperties(ValueType type) {
    return setAllProperties(type, TestContext.get().getStructureDepth());
  }


  /**
   * Set all properties to the same type of value.
   *
   * @param type  the value type
   * @param depth the maximum number of times a class can occur in the stack
   */
  public BeanHolder setAllProperties(ValueType type, int depth) {
    boolean useNulls = ValueFactory.getStructureDepth() >= depth;
    return setAllProperties(type, useNulls);
  }


  /**
   * Set all properties to the same type of value.
   *
   * @param type     the value type
   * @param useNulls if true, use nulls for all nullable properties
   */
  public BeanHolder setAllProperties(ValueType type, boolean useNulls) {
    for (String propertyName : getPropertyNames()) {
      if (useNulls && isNullable(propertyName)) {
        setProperty(propertyName, null);
      } else {
        setProperty(propertyName, createValue(type, propertyName));
      }
    }
    return this;
  }


  private void setCreatorData() {
    LinkedHashMap<String, Object> creatorParams = new LinkedHashMap<>(initialValues);

    HashSet<String> creatorKeys = new HashSet<>();

    for (var entry : values.entrySet()) {
      String name = entry.getKey();

      Property beanProperty = description.getProperty(name);
      boolean beanWritable = beanProperty != null && beanProperty.isWritable();

      Property creatorProperty = description.getBeanCreator().getProperty(name);
      boolean creatorWritable = creatorProperty != null && creatorProperty.isWritable();

      // We set this property in the creator unless we are preferring writers, and it is bean-writable.
      if (
          creatorWritable && !(preferWriters && beanWritable)
      ) {
        creatorKeys.add(name);
        creatorParams.remove(name);
        creatorParams.put(name, entry.getValue());
      }
    }
    creatorData = new CreatorData(creatorParams, creatorKeys);
  }


  /**
   * Set the property value. Note that this does not invoke the setter methods.
   *
   * @param name  the name of the property to set
   * @param value the value to set the property to
   *
   * @return true if the property value was changed, false otherwise
   */
  public boolean setProperty(String name, Object value) {
    if (value != null) {
      if (Objects.equals(readExpected(name), value)) {
        // Non-null value is the same as the expected value - so no change
        return false;
      }
    } else {
      if (hasExpected(name) && readExpected(name) == null) {
        // Null value is the same as the expected value - so no change
        return false;
      }
    }

    values.remove(name);
    values.put(name, value);

    return true;
  }


  @Override
  public String toString() {
    TreeMap<String, Object> myValues = new TreeMap<>(initialValues);
    myValues.putAll(values);
    return "BeanHolder(" + description.getBeanClass() + ", values=" + myValues + ")";
  }


  /** Verify that a property has the expected value. */
  public void verify(String propertyName) {
    Object actual = readActual(propertyName);
    Object expected = readExpected(propertyName);
    if (!Objects.equals(actual, expected)) {
      // Expected does not equal actual, but it could be a result of null or omitted handling. In either case, expected will be null.
      if (expected != null) {
        // Different values and not a special case, so failure.
        throw new AssertionException(
            "Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" is \"" + actual + "\" expected \"" + expected + "\".");
      }

      // Pass to appropriate special case handler
      if (hasExpected(propertyName)) {
        // We explicitly set it to null
        verifyExpectedNull(propertyName, actual);
      } else {
        // We omitted it
        verifyOmitted(propertyName, actual);
      }

    }
  }


  /** Handle special case where we explicitly set a property to null, but the actual value was not null. */
  private void verifyExpectedNull(String propertyName, Object actual) {
    // How did we set it?
    Property property;
    if (creatorData.keys.contains(propertyName)) {
      // We set it in the creator, so must be a creator property
      property = description.getBeanCreator().getProperty(propertyName);
    } else {
      property = description.getProperty(propertyName);
    }

    if (property == null || property.isIgnored()) {
      return;
    }

    // Does the behaviour match the property?
    NullBehaviour behaviour = property.getNullBehaviour();

    if (behaviour == NullBehaviour.NULL) {
      throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.ERROR) {
      throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
          + behaviour + " but did not throw an exception and actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.VALUE) {
      // Could be OK
      Object expected = property.getNullValue();
      if (!Objects.equals(expected, actual)) {
        // Sadly, not OK
        throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
            + behaviour + " but actual value was: " + actual + " expected: " + expected);
      }

      // OK - either are values are equal or the values was not specified.
      return;
    }

    // Behaviour not specified or was NOT_READABLE.
    if (behaviour != null) {
      throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    // No behavior specified, so we can't check.
    throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName
        + "\" was set to null with no on-null behaviour specified, but actual value was: " + actual);
  }


  /** Value was never set, but somehow had a non-null value. */
  private void verifyOmitted(String propertyName, Object actual) {
    Property property = description.getProperty(propertyName);
    if (property == null || property.isIgnored()) {
      // It's not something we check
      return;
    }

    NullBehaviour behaviour = property.getOmittedBehaviour();

    if (behaviour == NullBehaviour.NULL) {
      throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.ERROR) {
      throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
          + behaviour + " but did not throw an exception and actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.VALUE) {
      // Could be OK
      Object expected = property.getOmittedValue();
      if (!Objects.equals(expected, actual)) {
        // Sadly, not OK
        throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
            + behaviour + " but actual value was: " + actual + " expected: " + expected);
      }

      // OK - either are values are equal or the values was not specified.
      return;
    }

    // Behaviour not specified or was NOT_READABLE.
    if (behaviour != null) {
      throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    // No behavior specified, so we can't check.
    throw new AssertionException("Class " + description.getBeanClass() + ": Property \"" + propertyName
        + "\" was omitted and has no on-omitted behaviour specified, but actual value was: " + actual);
  }

}
