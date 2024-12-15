package io.setl.beantester.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.ValueFactoryRepository;
import io.setl.beantester.factories.ValueType;
import io.setl.beantester.util.AssertionUtils;

/**
 * The bean holder holds a bean and manages its creation.
 */
public class BeanHolder {

  private final HashSet<String> changed = new HashSet<>();

  private final BeanInformation information;

  private final HashMap<String, Object> initialValues = new HashMap<>();

  /** The current property values of the bean. */
  private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

  private Object bean;

  private TestContext testContext;


  public BeanHolder(BeanInformation information) {
    this.testContext = information.testContext();
    this.information = information;

    ValueFactoryRepository vfr = information.testContext().getValueFactoryRepository();

    // Set a value for all non-null creator values.
    for (PropertyInformation info : information.beanCreator().properties()) {
      if (!info.nullable()) {
        initialValues.put(info.name(), vfr.create(ValueType.PRIMARY, information, info));
      }
    }
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
    TreeMap<String, Object> creatorParams = new TreeMap<>(initialValues);
    HashSet<String> creatorKeys = new HashSet<>();

    for (String name : changed) {
      PropertyInformation infoBean = information.property(name);
      boolean beanWritable = infoBean != null && infoBean.writable();

      PropertyInformation infoCreator = information.beanCreator().property(name);
      boolean creatorWritable = infoCreator != null && infoCreator.writable();

      if (
          creatorWritable
              && !(testContext.preferWriters() && beanWritable)
      ) {
        creatorKeys.add(name);
        creatorParams.put(name, values.get(name));
      }
    }

    if (bean == null || !creatorKeys.isEmpty()) {
      try {
        bean = information.beanCreator().exec(creatorParams);
      } catch (Throwable e) {
        throw new IllegalStateException("Failed to create bean", e);
      }
    }

    for (var entry : values.entrySet()) {
      String name = entry.getKey();
      if (creatorKeys.contains(name)) {
        continue;
      }

      Object value = entry.getValue();
      PropertyInformation info = information.property(name);

      if (info != null && info.writable()) {
        info.write(bean, value);
      }
    }

  }


  public Object createValue(ValueType type, String propertyName) {
    PropertyInformation info = information.beanCreator().property(propertyName);
    if (info == null) {
      info = information.property(propertyName);
    }
    if (info == null) {
      throw new IllegalArgumentException("No property named " + propertyName);
    }
    return information.testContext().getValueFactoryRepository().create(type, information, information.property(propertyName));
  }


  public Class<?> getBeanClass() {
    return information.beanClass();
  }


  /**
   * Get all the non-ignored property names.
   *
   * @return the property names
   */
  public Collection<String> getPropertyNames() {
    TreeSet<String> names = new TreeSet<>();
    information.properties().stream().filter(i -> !i.ignored()).forEach(p -> names.add(p.name()));
    information.beanCreator().properties().stream().filter(i -> !i.ignored()).forEach(p -> names.add(p.name()));
    return names;
  }


  /**
   * Is a property nullable?
   *
   * @param propertyName the property name
   *
   * @return true if the property is nullable, false otherwise
   */
  public boolean isNullable(String propertyName) {
    PropertyInformation info = information.property(propertyName);

    //The property is only nullable if it is nullable everywhere.
    if (info != null && !info.nullable()) {
      return false;
    }
    info = information.beanCreator().property(propertyName);
    return info == null || info.nullable();
  }


  /**
   * Is the property significant for equality testing?
   *
   * @param propertyName the property name
   *
   * @return true if the property is significant, false otherwise
   */
  public boolean isSignificant(String propertyName) {
    PropertyInformation info = information.property(propertyName);
    if (info != null && info.significant() && info.writable()) {
      return true;
    }
    info = information.beanCreator().property(propertyName);
    return info != null && info.significant() && info.writable();
  }


  /**
   * Is the property testable? A testable property is readable and writable.
   *
   * @param propertyName the property name
   *
   * @return true if the property is testable, false otherwise
   */
  public boolean isTestable(String propertyName) {
    boolean isIgnored = false;
    boolean isWritable = false;
    boolean isReadable = false;

    PropertyInformation info = information.property(propertyName);
    if (info != null) {
      isIgnored = info.ignored();
      isWritable = info.writable();
      isReadable = info.readable();
    }

    info = information.beanCreator().property(propertyName);
    if (info != null) {
      isIgnored = isIgnored || info.ignored();
      isWritable = isWritable || info.writable();
      isReadable = isReadable || info.readable();
    }

    return isWritable && isReadable && !isIgnored;
  }


  public Object readActual(String propertyName) {
    buildBean();
    return information.property(propertyName).read(bean);
  }


  public Object readExpected(String propertyName) {
    Object o = values.get(propertyName);
    if (o == null && !values.containsKey(propertyName)) {
      o = initialValues.get(propertyName);
    }
    return o;
  }


  public void reset() {
    bean = null;
    values.clear();
    changed.clear();
  }


  public void setProperty(String propertyName, Object value) {
    values.remove(propertyName);
    values.put(propertyName, value);
    changed.add(propertyName);
  }


  public TestContext testContext() {
    return testContext;
  }


  /** Verify that a property has the expected value. */
  public void verify(String propertyName) {
    Object actual = readActual(propertyName);
    Object expected = readExpected(propertyName);
    if (!Objects.equals(actual, expected)) {
      AssertionUtils.fail("Property \"" + propertyName + "\" is \"" + actual + "\" expected \"" + expected + "\".");
    }
  }

}
