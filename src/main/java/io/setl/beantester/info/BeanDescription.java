package io.setl.beantester.info;

import io.setl.beantester.TestContext;
import io.setl.beantester.info.Specs.BeanCreator;

/**
 * Defines an object that provides information about a JavaBean.
 *
 * @author Graham Williamson
 */
public class BeanDescription extends AbstractModel<BeanDescription> {

  /**
   * Create BeanInformation for a specified class.
   */
  public static BeanDescription create(TestContext testContext, Class<?> beanClass, Specs.Spec... specs) {
    return new BeanDescriptionFactory().create(testContext, beanClass, specs);
  }


  private final Class<?> beanClass;

  private final TestContext testContext;

  private BeanCreator<?> beanCreator;


  public BeanDescription(TestContext testContext, Class<?> beanClass) {
    this.testContext = testContext;
    this.beanClass = beanClass;
  }


  /**
   * Get the type of bean this object contains information about.
   *
   * @return The type of bean this object contains information about.
   */
  public Class<?> beanClass() {
    return beanClass;
  }


  public BeanCreator<?> beanCreator() {
    return beanCreator;
  }


  public BeanDescription beanCreator(BeanCreator<?> beanCreator) {
    this.beanCreator = beanCreator;
    return this;
  }


  public BeanHolder createHolder() {
    return new BeanHolder(this);
  }


  public TestContext testContext() {
    return testContext;
  }


  @Override
  public String toString() {
    return "BeanInformation{"
        + "beanClass=" + beanClass
        + ", properties=" + properties()
        + ", beanCreator=" + beanCreator
        + '}';
  }

}
