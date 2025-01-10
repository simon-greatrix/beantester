package io.setl.beantester.info.specs;

import java.util.List;

import io.setl.beantester.info.Specs.Spec;

/**
 * An interface for filtering specs. The description factory will look for a class that implements this interface with the bean class name suffixed with
 * "$SpecFilter" (configurable in TestContext).
 *
 * <p>A SpecFilter class must have a zero-argument public constructor.</code></p>
 */
public interface SpecFilter {

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
   * Filter the list of specs.
   *
   * @param specs the list of specs to filter. This list may be modified.
   *
   * @return the filtered list of specs
   */
  List<Spec> filter(List<Spec> specs);

}
