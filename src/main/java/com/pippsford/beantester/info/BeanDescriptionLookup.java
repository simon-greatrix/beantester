package com.pippsford.beantester.info;

import java.util.Optional;

import com.pippsford.beantester.info.Specs.Spec;

/**
 * A lookup for bean descriptions.
 */
public interface BeanDescriptionLookup {

  /**
   * Get the description of a bean. The description may silent ignore the additional specifiers.
   *
   * @param type  the type of the bean
   * @param specs additional specifiers for the bean
   *
   * @return the description, or an empty optional if this lookup cannot create a description for the bean
   */
  Optional<BeanDescription> getDescription(Class<?> type, Spec... specs);

}
