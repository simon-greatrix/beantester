package io.setl.beantester.info;

/**
 * Defines an object that provides information about a JavaBean.
 */
public class BeanDescription extends AbstractModel<BeanDescription> {

  /**
   * Create BeanInformation for a specified class.
   */
  public static BeanDescription create(Class<?> beanClass, Specs.Spec... specs) {
    return new BeanDescriptionFactory(beanClass).create(specs);
  }


  private final Class<?> beanClass;

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
    super(beanDescription.properties());
    this.beanClass = beanDescription.beanClass;
    this.beanCreator = beanDescription.beanCreator.copy();
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


  @Override
  public String toString() {
    return "BeanInformation{"
        + "beanClass=" + beanClass
        + ", properties=" + properties()
        + ", beanCreator=" + beanCreator
        + '}';
  }

}
