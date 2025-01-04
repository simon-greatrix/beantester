package io.setl.beantester.info;


import java.util.Map;
import java.util.function.Function;

/**
 * Specify to create a bean using the specified function. The function takes a map of parameter names to values and should create the bean accordingly.
 */
public interface BeanCreator<B extends BeanCreator<B> & Model<B>> extends
                                                                  Function<Map<String, Object>, Object>,
                                                                  Model<B> {

  /**
   * Create a copy of this creator.
   *
   * @return a copy
   */
  BeanCreator<B> copy();

  /**
   * Set the owner of this creator. If the properties are changed, the owner is notified.
   *
   * @param owner the owner of this creator
   */
  void setOwner(BeanDescription owner);

  /**
   * Perform validation on the builder.
   *
   * @param beanDescription the bean description to validate. This will use the bean creator;
   */
  default void validate(BeanDescription beanDescription) {
    // do nothing
  }

}
