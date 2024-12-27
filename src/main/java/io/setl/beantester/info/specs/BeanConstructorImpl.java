package io.setl.beantester.info.specs;

import java.util.Collections;
import java.util.List;

import io.setl.beantester.info.Specs.BeanConstructor;

/**
 * A specification for a bean constructor.
 *
 * @param names the names of the parameters
 * @param types the types of the parameters
 */
public record BeanConstructorImpl(List<String> names, List<Class<?>> types) implements BeanConstructor {

  public BeanConstructorImpl(List<String> names, List<Class<?>> types) {
    this.names = Collections.unmodifiableList(names);
    this.types = Collections.unmodifiableList(types);
  }

}
