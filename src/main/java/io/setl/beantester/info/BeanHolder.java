package io.setl.beantester.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.ValueFactory;
import io.setl.beantester.factories.ValueFactoryRepository;
import io.setl.beantester.factories.ValueType;

/**
 * The bean holder holds a bean and manages its creation.
 */
public class BeanHolder implements ValueFactory<Object> {

  private record CreatorData(TreeMap<String, Object> params, HashSet<String> keys) {

  }



  private final HashSet<String> changed = new HashSet<>();

  private final BeanDescription info;

  private final HashMap<String, Object> initialValues = new HashMap<>();

  private final TestContext testContext;

  private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

  private Object bean;


  /**
   * New instance.
   *
   * @param info the description of the bean
   */
  public BeanHolder(BeanDescription info) {
    this.testContext = info.testContext();
    this.info = info;

    ValueFactoryRepository vfr = info.testContext().getValueFactoryRepository();

    // Set a value for all non-null values.
    for (Property property : info.beanCreator().properties()) {
      if (!property.nullable()) {
        initialValues.put(property.name(), vfr.create(ValueType.PRIMARY, info, property));
      }
    }
    for (Property property : info.properties()) {
      if (!property.nullable()) {
        initialValues.put(property.name(), vfr.create(ValueType.PRIMARY, info, property));
      }
    }
  }


  private BeanHolder(BeanHolder copy) {
    this.testContext = copy.testContext;
    this.info = copy.info;

    this.initialValues.putAll(copy.initialValues);
    this.changed.addAll(copy.changed);
    this.values.putAll(copy.values);
    this.bean = null;
  }


  /**
   * Get the bean. Every call to this creates a new instance.
   *
   * @return the bean instance
   */
  public Object bean() {
    buildBean();
    Object built = bean;
    bean = null;
    return built;
  }


  private void buildBean() {
    CreatorData creatorData = creatorData();

    if (bean == null || !creatorData.keys.isEmpty()) {
      try {
        bean = info.beanCreator().exec(creatorData.params);
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
      CreatorData createData = creatorData();
      try {
        return Optional.of(builder.build(createData.params));
      } catch (Throwable e) {
        throw new AssertionError("Failed to create builder for class " + info.beanClass(), e);
      }
    }

    return Optional.empty();
  }


  public BeanHolder copy() {
    return new BeanHolder(this);
  }


  @Override
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
    return this.info.testContext().getValueFactoryRepository().create(type, this.info, info);
  }


  private CreatorData creatorData() {
    TreeMap<String, Object> creatorParams = new TreeMap<>(initialValues);
    HashSet<String> creatorKeys = new HashSet<>();

    for (String name : changed) {
      Property infoBean = info.property(name);
      boolean beanWritable = infoBean != null && infoBean.writable();

      Property infoCreator = info.beanCreator().property(name);
      boolean creatorWritable = infoCreator != null && infoCreator.writable();

      if (
          creatorWritable
              && !(testContext.preferWriters() && beanWritable)
      ) {
        creatorKeys.add(name);
        creatorParams.put(name, values.get(name));
      }
    }
    return new CreatorData(creatorParams, creatorKeys);
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
    if (info != null && !info.nullable()) {
      return false;
    }
    info = this.info.beanCreator().property(name);
    return info == null || info.nullable();
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
      isReadable = isReadable || info.readable();
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
   * Read the actual value of a property. Note that this may trigger bean creation.
   *
   * @param name the property name
   *
   * @return the actual value
   */
  public Object readActual(String name) {
    buildBean();
    return info.property(name).read(bean);
  }


  /**
   * Read the expected value of a property.
   *
   * @param name the property name
   *
   * @return the expected value
   */
  public Object readExpected(String name) {
    Object o = values.get(name);
    if (o == null && !values.containsKey(name)) {
      o = initialValues.get(name);
    }
    return o;
  }


  /** Reset all the property values. */
  public void reset() {
    bean = null;
    values.clear();
    changed.clear();
  }


  /**
   * Set all properties to the same type of value.
   *
   * @param type     the value type
   * @param useNulls true if nullable properties should be set to null
   */
  public void setAllProperties(ValueType type, boolean useNulls) {
    for (String propertyName : getPropertyNames()) {
      if (useNulls && isNullable(propertyName)) {
        setProperty(propertyName, null);
      } else {
        setProperty(propertyName, createValue(type, propertyName));
      }
    }
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
    if (Objects.equals(readExpected(name), value)) {
      return false;
    }

    values.remove(name);
    values.put(name, value);
    changed.add(name);

    return true;
  }


  public TestContext testContext() {
    return testContext;
  }


  /** Verify that a property has the expected value. */
  public void verify(String propertyName) {
    Object actual = readActual(propertyName);
    Object expected = readExpected(propertyName);
    if (!Objects.equals(actual, expected)) {
      throw new AssertionError("Class " + info.beanClass() + ": Property \"" + propertyName + "\" is \"" + actual + "\" expected \"" + expected + "\".");
    }
  }

}
