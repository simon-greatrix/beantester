package io.setl.beantester.info;

import io.setl.beantester.info.Specs.Spec;

public interface BeanDescriptionLookup {

  /**
   * Get the description of a bean. The description may silent ignore the additional specifiers.
   *
   * @param type  the type of the bean
   * @param specs additional specifiers for the bean
   *
   * @return the description
   */
  BeanDescription getDescription(Class<?> type, Spec... specs);

  /**
   * Test if this lookup can provide a description of a bean. The description may silent ignore the additional specifiers.
   *
   * @param type  the type of the bean
   * @param specs additional specifiers for the bean
   *
   * @return true if a description is available
   */
  boolean hasDescription(Class<?> type, Spec... specs);

}
