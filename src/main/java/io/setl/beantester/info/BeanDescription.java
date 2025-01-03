package io.setl.beantester.info;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

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
    return new BeanDescriptionFactory(beanClass, specs, true).create(specs);
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
