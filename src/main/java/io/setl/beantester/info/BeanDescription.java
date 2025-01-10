package io.setl.beantester.info;

import java.util.Optional;

import lombok.Getter;

import io.setl.beantester.TestContext;

/**
 * Defines an object that provides information about a JavaBean.
 */
@Getter
public class BeanDescription extends AbstractModel<BeanDescription> {

  /**
   * Create BeanInformation for a specified class.
   */
  public static BeanDescription create(Class<?> beanClass, Specs.Spec... specs) {
    Optional<BeanDescription> lookup = TestContext.get().findBeanDescriptionLookup(beanClass, specs);
    if (lookup.isPresent()) {
      return lookup.get();
    }

    // Use default factory
    return new BeanDescriptionFactory(beanClass, specs, true).create();
  }


  private final Class<?> beanClass;

  private BeanCreator<?> beanCreator;

  @Getter
  private int revision = 0;


  public BeanDescription(Class<?> beanClass) {
    this.beanClass = beanClass;
  }


  /**
   * Copy constructor.
   *
   * @param beanDescription the description to copy
   */
  public BeanDescription(BeanDescription beanDescription) {
    super(beanDescription.getProperties());
    this.beanClass = beanDescription.beanClass;
    this.beanCreator = beanDescription.beanCreator.copy();
    this.beanCreator.setOwner(this);
  }


  public BeanHolder createHolder() {
    return new BeanHolder(this);
  }


  @Override
  public void notifyChanged(Property property) {
    super.notifyChanged(property);
    revision++;
  }


  /**
   * Set the bean creator that will be used to create instances of the bean.
   *
   * @param newBeanCreator the new bean creator
   *
   * @return this
   */
  public BeanDescription setBeanCreator(BeanCreator<?> newBeanCreator) {
    if (beanCreator != null) {
      beanCreator.setOwner(null);
    }

    this.beanCreator = newBeanCreator;

    if (newBeanCreator != null) {
      newBeanCreator.setOwner(this);
    }

    return this;
  }


  @Override
  public String toString() {
    return "BeanInformation{"
        + "beanClass=" + beanClass
        + ", properties=" + getProperties()
        + ", beanCreator=" + beanCreator
        + '}';
  }

}
