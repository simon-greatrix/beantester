package io.setl.beantester.info;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines an object that provides information about a JavaBean.
 */
@Getter
public class BeanDescription extends AbstractModel<BeanDescription> {

  /**
   * Create BeanInformation for a specified class.
   */
  public static BeanDescription create(Class<?> beanClass, Specs.Spec... specs) {
    return new BeanDescriptionFactory(beanClass).create(specs);
  }


  private final Class<?> beanClass;

  @Setter
  private BeanCreator<?> beanCreator;


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
  }


  public BeanHolder createHolder() {
    return new BeanHolder(this);
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
