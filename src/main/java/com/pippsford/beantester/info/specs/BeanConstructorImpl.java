package com.pippsford.beantester.info.specs;

import java.util.Collections;
import java.util.List;

import lombok.Value;

import com.pippsford.beantester.info.Specs.BeanConstructor;

/**
 * A specification for a bean constructor.
 */
@Value
public class BeanConstructorImpl implements BeanConstructor {

  List<String> names;

  List<Class<?>> types;


  /**
   * New instance.
   *
   * @param names the names of the parameters
   * @param types the types of the parameters
   */
  public BeanConstructorImpl(List<String> names, List<Class<?>> types) {
    this.names = Collections.unmodifiableList(names);
    this.types = Collections.unmodifiableList(types);
  }

}
