package com.pippsford.beantester.info.specs;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.info.Specs.Spec;

/**
 * An interface for filtering specs. The description factory will look for a class that implements this interface with the bean class name suffixed with
 * "$SpecFilter" (configurable in TestContext).
 *
 * <p>A SpecFilter class must have a zero-argument public constructor.</p>
 */
public interface SpecFilter {

  /** A default SpecFilter that does nothing. */
  SpecFilter DO_NOTHING = new SpecFilter() {
  };

  /** Known filters. */
  ConcurrentHashMap<Class<?>, SpecFilter> FILTERS = new ConcurrentHashMap<>();

  /**
   * Get the SpecFilter applicable to the given bean class.
   *
   * @param beanClass the bean class
   *
   * @return the SpecFilter
   */
  static SpecFilter getSpecFilter(Class<?> beanClass) {
    return FILTERS.computeIfAbsent(beanClass, SpecFilter::getSpecFilterInternal);
  }

  private static SpecFilter getSpecFilterInternal(Class<?> beanClass) {
    String suffix = TestContext.get().getSpecSuffix();
    if (suffix.isEmpty()) {
      return DO_NOTHING;
    }

    Class<?> testClass = beanClass;
    while (testClass != null) {
      try {
        Class<?> specClass = Class.forName(testClass.getName() + suffix);
        if (SpecFilter.class.isAssignableFrom(specClass)) {
          return (SpecFilter) specClass.getConstructor().newInstance();
        }
      } catch (ReflectiveOperationException e) {
        // do nothing
      }
      testClass = testClass.getSuperclass();
    }
    return DO_NOTHING;
  }

  /**
   * Test if the list of specifications contains a specific type of spec.
   *
   * @param type  the type of spec to test for
   * @param specs the list of specs to test
   *
   * @return true if the list contains a spec of the given type
   */
  static boolean hasSpec(Class<? extends Spec> type, List<Spec> specs) {
    return specs.stream().anyMatch(type::isInstance);
  }

  /**
   * Remove all specs of a specific type from the list.
   *
   * @param type  the type of spec to remove
   * @param specs the list of specs to remove from
   *
   * @return true if any specs were removed
   */
  static boolean removeSpec(Class<? extends Spec> type, List<Spec> specs) {
    return specs.removeIf(type::isInstance);
  }

  /**
   * Invoked once when a ValueFactory is created that creates objects of the type this SpecFilter is associated with.
   */
  default void beforeAll() {
    // do nothing
  }

  /**
   * Filter the list of specs.
   *
   * @param specs the list of specs to filter. This list may be modified.
   *
   * @return the filtered list of specs
   */
  default List<Spec> filter(List<Spec> specs) {
    return specs;
  }

  default void postCreate() {
    // do nothing
  }

  default void preCreate() {
    // do nothing
  }

}
