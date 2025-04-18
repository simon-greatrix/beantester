package com.pippsford.beantester.info;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.pippsford.beantester.AssertionException;
import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.factories.FactoryRepository;
import com.pippsford.beantester.info.Specs.BuilderMethods;
import com.pippsford.beantester.info.Specs.Spec;

/**
 * Specification of a bean creator that uses a builder pattern.
 */
public class BeanBuilder extends AbstractCreatorModel<BeanBuilder> {

  private final Class<?> beanClass;

  private final BuilderMethods builderMethods;


  /**
   * New instance.
   *
   * @param beanClass      the class being built by the builder
   * @param builderMethods the methods to create the builder and the bean
   * @param specs          the specifications for the bean
   */
  public BeanBuilder(
      Class<?> beanClass,
      BuilderMethods builderMethods,
      Spec... specs
  ) {
    this.beanClass = beanClass;
    this.builderMethods = builderMethods;

    Class<?> builderClass;
    try {
      builderClass = builderMethods.getBuilderSupplier().exec().getClass();
    } catch (Throwable e) {
      throw new IllegalStateException("Failed to create builder", e);
    }

    Collection<Property> foundProperties = new BeanDescriptionFactory(builderClass, specs, false).findWritableProperties();

    for (Property property : foundProperties) {
      setProperty(property);
    }
  }


  /**
   * Copy constructor.
   *
   * @param beanBuilder the bean builder to copy
   */
  public BeanBuilder(BeanBuilder beanBuilder) {
    super(beanBuilder.getProperties());
    this.beanClass = beanBuilder.beanClass;
    this.builderMethods = beanBuilder.builderMethods;
  }


  @Override
  public Object apply(Map<String, Object> values) {
    try {
      Object builder = build(values);
      return builderMethods.getBuildFunction().exec(builder);
    } catch (Throwable t) {
      throw new AssertionException("Failed to build bean", t);
    }
  }


  /**
   * Create a builder, set the properties and build the object.
   *
   * @param values the values for the builder
   *
   * @return the builder
   */
  public Object build(Map<String, Object> values) {
    Object builder;
    try {
      builder = builderMethods.getBuilderSupplier().exec();
    } catch (Throwable e) {
      throw new AssertionException("Class " + beanClass + " : Failed to create builder", e);
    }
    for (Property property : getProperties()) {
      String name = property.getName();
      if (values.containsKey(name)) {
        property.write(builder, values.get(name));
      }
    }
    return builder;
  }


  @Override
  public BeanCreator<BeanBuilder> copy() {
    return new BeanBuilder(this);
  }


  /**
   * Validate the builder. Specifying a value for the same property twice should not cause an error, and the second value should take precedence.
   *
   * @param beanDescription the bean description to validate. This will use the bean creator.
   */
  @Override
  public void validate(BeanDescription beanDescription) {
    // Find all properties that can be written to in the builder and then read back from the bean.

    // All writable properties
    Set<String> writable = getProperties().stream()
        .filter(Property::isWritable)
        .filter(not(Property::isIgnored))
        .map(Property::getName)
        .collect(Collectors.toSet());

    // Remove non-readable
    Set<String> readable = beanDescription.getProperties().stream()
        .filter(Property::isReadable)
        .filter(not(Property::isIgnored))
        .map(Property::getName)
        .collect(Collectors.toSet());

    // Find the union of readable and writable properties
    Set<String> namesToTest = new HashSet<>();
    for (String name : writable) {
      if (readable.contains(name)) {
        namesToTest.add(name);
      }
    }

    // Create initial values for the properties
    FactoryRepository factories = TestContext.get().getFactories();
    Map<String, Object> primaryValues = new HashMap<>();
    Map<String, Object> secondaryValues = new HashMap<>();
    for (String name : namesToTest) {
      Property property = getProperty(name);
      primaryValues.put(name, factories.create(beanClass, property, ValueType.PRIMARY));
      secondaryValues.put(name, factories.create(beanClass, property, ValueType.SECONDARY));
    }

    // Do tests
    for (String name : namesToTest) {
      Property property = getProperty(name);
      Object builder = build(primaryValues);

      // Write the secondary value to the builder, over-riding the primary value
      property.write(builder, secondaryValues.get(name));

      // Make the bean.
      Object bean;
      try {
        bean = builderMethods.getBuildFunction().exec(builder);
      } catch (Throwable e) {
        throw new AssertionException("Class " + beanClass + " : Failed to build bean", e);
      }

      // Verify the property is set correctly
      Object actual = beanDescription.getProperty(name).read(bean);
      if (!Objects.equals(secondaryValues.get(name), actual)) {
        throw new AssertionException(
            "Class " + beanClass + " : Property " + name
                + " builder override failed. Expected " + secondaryValues.get(name)
                + " but got " + actual);
      }
    }
  }

}
