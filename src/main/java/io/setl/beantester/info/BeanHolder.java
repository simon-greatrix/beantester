package io.setl.beantester.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import io.setl.beantester.AssertionException;
import io.setl.beantester.NullBehaviour;
import io.setl.beantester.TestContext;
import io.setl.beantester.ValueType;
import io.setl.beantester.factories.FactoryRepository;

/**
 * The bean holder holds a bean and manages its creation.
 */
public class BeanHolder {

  private record CreatorData(LinkedHashMap<String, Object> params, HashSet<String> keys) {

  }



  private final BeanDescription info;

  private final HashMap<String, Object> initialValues = new HashMap<>();

  /** The property values (may be inconsistent with the bean). */
  private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

  /** The current bean. */
  private Object bean;

  /** Creator data used in creating the current bean. */
  private CreatorData creatorData;

  /** Whether to prefer writers to creators. */
  private boolean preferWriters = TestContext.get().preferWriters();


  /**
   * New instance.
   *
   * @param info the description of the bean
   */
  public BeanHolder(BeanDescription info) {
    this.info = info;
    resetInitialValues();
  }


  private BeanHolder(BeanHolder copy) {
    this.info = copy.info;

    this.initialValues.putAll(copy.initialValues);
    this.values.putAll(copy.values);
    this.bean = null;
  }


  /**
   * Get the bean. Every call to this creates a new instance.
   *
   * @return the bean instance
   */
  public Object bean() {
    bean = null;
    buildBean();
    return bean;
  }


  private void buildBean() {
    setCreatorData();

    if (bean == null || !creatorData.keys.isEmpty()) {
      try {
        bean = info.beanCreator().apply(creatorData.params);
      } catch (Throwable e) {
        throw new IllegalStateException("Failed to create bean of type " + info.beanClass(), e);
      }
    }

    for (var entry : values.entrySet()) {
      String name = entry.getKey();
      if (creatorData.keys.contains(name)) {
        continue;
      }

      Object value = entry.getValue();
      Property info = this.info.property(name);

      if (info != null && info.writable()) {
        info.write(bean, value);
      }
    }

  }


  /**
   * Create a builder with the current property values. If the bean does not user a builder, returns an empty Optional.
   *
   * @return the created builder
   */
  public Optional<Object> builder() {
    if (info.beanCreator() instanceof BeanBuilder builder) {
      setCreatorData();
      try {
        return Optional.of(builder.build(creatorData.params));
      } catch (Throwable e) {
        throw new AssertionException("Failed to create builder for class " + info.beanClass(), e);
      }
    }

    return Optional.empty();
  }


  public BeanHolder copy() {
    return new BeanHolder(this);
  }


  public Object create(ValueType type) {
    setAllProperties(type, true);
    return bean();
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
    Property info = this.info.beanCreator().property(name);
    if (info == null) {
      info = this.info.property(name);
    }
    if (info == null) {
      throw new IllegalArgumentException("No property named " + name);
    }
    return TestContext.get().getFactories().create(type, this.info.beanClass(), info);
  }


  public Class<?> getBeanClass() {
    return info.beanClass();
  }


  /**
   * Get all the non-ignored writable property names.
   *
   * @return the property names
   */
  public Collection<String> getPropertyNames() {
    TreeSet<String> names = new TreeSet<>();
    info.properties().stream()
        .filter(i -> !i.ignored())
        .filter(Property::writable)
        .forEach(p -> names.add(p.name()));
    info.beanCreator().properties().stream()
        .filter(i -> !i.ignored())
        .filter(Property::writable)
        .forEach(p -> names.add(p.name()));
    return names;
  }


  /**
   * Find all the properties that can be set via the creator or by a bean setter.
   *
   * @return the set of property names
   */
  public Set<String> getSwitchableProperties() {
    Set<String> set = new HashSet<>();
    for (Property beanProperty : info.properties()) {
      if (beanProperty.writable()) {
        Property creatorProperty = info.beanCreator().property(beanProperty.name());
        if (creatorProperty != null && creatorProperty.writable()) {
          set.add(beanProperty.name());
        }
      }
    }

    return set;
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


  public BeanDescription information() {
    return info;
  }


  /**
   * Is a property nullable? A property can only be nullable if both the creator and the bean consider it to be nullable.
   *
   * @param name the property name
   *
   * @return true if the property is nullable, false otherwise
   */
  public boolean isNullable(String name) {
    Property info = this.info.property(name);

    //The property is only nullable if it is nullable everywhere.
    if (info != null && info.notNull()) {
      return false;
    }
    info = this.info.beanCreator().property(name);
    return info == null || !info.notNull();
  }


  /**
   * Is a property readable? A property can be readable only in the bean.
   *
   * @param name the property name
   *
   * @return true if the property is readable, false otherwise
   */
  public boolean isReadable(String name) {
    Property info = this.info.property(name);
    return info != null && info.readable();
  }


  /**
   * Is the property significant for equality testing? This must be set explicitly.
   *
   * @param name the property name
   *
   * @return true if the property is significant, false otherwise
   */
  public boolean isSignificant(String name) {
    Property info = this.info.property(name);
    if (info != null && info.significant() && info.writable()) {
      return true;
    }
    info = this.info.beanCreator().property(name);
    return info != null && info.significant() && info.writable();
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

    Property info = this.info.property(name);
    if (info != null) {
      isIgnored = info.ignored();
      isWritable = info.writable();
      isReadable = info.readable();
    }

    info = this.info.beanCreator().property(name);
    if (info != null) {
      isIgnored = isIgnored || info.ignored();
      isWritable = isWritable || info.writable();
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
    Property info = this.info.property(name);
    if (info != null && info.writable()) {
      return true;
    }
    info = this.info.beanCreator().property(name);
    return info != null && info.writable();
  }


  /**
   * Does this bean prefer writers or creators?.
   *
   * @return true if the bean prefers writers, false otherwise
   */
  public boolean preferWriters() {
    return preferWriters;
  }


  /**
   * Set whether this bean prefers writers or creators.
   *
   * @param prefer true if the bean prefers writers, false otherwise
   *
   * @return this
   */
  public BeanHolder preferWriters(boolean prefer) {
    preferWriters = prefer;
    return this;
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
    Property property = info.property(name);
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
    for (Property property : info.beanCreator().properties()) {
      if (property.notNull()) {
        initialValues.put(property.name(), vfr.create(ValueType.PRIMARY, getBeanClass(), property));
      }
    }

    for (Property property : info.properties()) {
      if (property.notNull()) {
        initialValues.put(property.name(), vfr.create(ValueType.PRIMARY, getBeanClass(), property));
      }
    }
  }


  /**
   * Set all properties to the same type of value.
   *
   * @param type     the value type
   * @param useNulls true if nullable properties should be set to null
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
    LinkedHashMap<String, Object> creatorParams = new LinkedHashMap<>();
    for (var entry : initialValues.entrySet()) {
      if (!values.containsKey(entry.getKey())) {
        creatorParams.put(entry.getKey(), entry.getValue());
      }
    }

    HashSet<String> creatorKeys = new HashSet<>();

    for (var entry : values.entrySet()) {
      String name = entry.getKey();
      Property beanProperty = info.property(name);
      boolean beanWritable = beanProperty != null && beanProperty.writable();

      Property creatorProperty = info.beanCreator().property(name);
      boolean creatorWritable = creatorProperty != null && creatorProperty.writable();

      // We set this property in the creator unless we are preferring writers and it is bean-writable.
      if (
          creatorWritable && !(preferWriters && beanWritable)
      ) {
        creatorKeys.add(name);
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
    return "BeanHolder(" + info.beanClass() + ", values=" + myValues + ")";
  }


  /** Verify that a property has the expected value. */
  public void verify(String propertyName) {
    Object actual = readActual(propertyName);
    Object expected = readExpected(propertyName);
    if (!Objects.equals(actual, expected)) {
      // Expected does not equal actual, but it could be a result of null or omitted handling. In either case, expected will be null.
      if (expected != null) {
        // Different values and not a special case, so failure.
        throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" is \"" + actual + "\" expected \"" + expected + "\".");
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
      property = info.beanCreator().property(propertyName);
    } else {
      property = info.property(propertyName);
    }

    if (property == null || property.ignored()) {
      return;
    }

    // Does the behaviour match the property?
    NullBehaviour behaviour = property.nullBehaviour();

    if (behaviour == NullBehaviour.NULL) {
      throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.ERROR) {
      throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
          + behaviour + " but did not throw an exception and actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.VALUE) {
      // Could be OK
      Object expected = property.nullValue();
      if (!Objects.equals(expected, actual)) {
        // Sadly, not OK
        throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
            + behaviour + " but actual value was: " + actual + " expected: " + expected);
      }

      // OK - either are values are equal or the values was not specified.
      return;
    }

    // Behaviour not specified or was NOT_READABLE.
    if (behaviour != null) {
      throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was set to null and has on-null behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    // No behavior specified, so we can't check.
    throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName
        + "\" was set to null with no on-null behaviour specified, but actual value was: " + actual);
  }


  /** Value was never set, but somehow had a non-null value. */
  private void verifyOmitted(String propertyName, Object actual) {
    Property property = info.property(propertyName);
    if (property == null || property.ignored()) {
      // It's not something we check
      return;
    }

    NullBehaviour behaviour = property.omittedBehaviour();

    if (behaviour == NullBehaviour.NULL) {
      throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.ERROR) {
      throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
          + behaviour + " but did not throw an exception and actual value was: " + actual);
    }

    if (behaviour == NullBehaviour.VALUE) {
      // Could be OK
      Object expected = property.omittedValue();
      if (!Objects.equals(expected, actual)) {
        // Sadly, not OK
        throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
            + behaviour + " but actual value was: " + actual + " expected: " + expected);
      }

      // OK - either are values are equal or the values was not specified.
      return;
    }

    // Behaviour not specified or was NOT_READABLE.
    if (behaviour != null) {
      throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName + "\" was omitted and has on-omitted behaviour of "
          + behaviour + " but actual value was: " + actual);
    }

    // No behavior specified, so we can't check.
    throw new AssertionException("Class " + info.beanClass() + ": Property \"" + propertyName
        + "\" was omitted and has no on-omitted behaviour specified, but actual value was: " + actual);
  }

}
