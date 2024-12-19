package io.setl.beantester.info.specs;

import java.util.Collections;
import java.util.List;

import io.setl.beantester.info.Specs.BeanConstructor;
import io.setl.beantester.info.Specs.BeanMaker;

/** A simple implementation of BeanMaker. */
public record BeanMakerImpl(Class<?> factoryClass, String factoryName, List<String> names, List<Class<?>> types) implements BeanMaker {

  public BeanMakerImpl(Class<?> factoryClass, String factoryName, List<String> names, List<Class<?>> types) {
    this.factoryClass = factoryClass;
    this.factoryName = factoryName;
    this.names = Collections.unmodifiableList(names);
    this.types = Collections.unmodifiableList(types);
  }


  public BeanMakerImpl(Class<?> factoryClass, String factoryName, BeanConstructor constructor) {
    this(factoryClass, factoryName, constructor.names(), constructor.types());
  }

}
