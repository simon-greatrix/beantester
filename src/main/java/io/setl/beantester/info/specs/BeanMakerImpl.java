package io.setl.beantester.info.specs;

import java.util.Collections;
import java.util.List;

import lombok.Value;

import io.setl.beantester.info.Specs.BeanConstructor;
import io.setl.beantester.info.Specs.BeanMaker;

/** A simple implementation of BeanMaker. */
@Value
public class BeanMakerImpl implements BeanMaker {

  Class<?> factoryClass;

  String factoryName;

  List<String> names;

  List<Class<?>> types;


  /**
   * New instance.
   *
   * @param factoryClass the class containing the factory method
   * @param factoryName  the name of the factory method
   * @param names        the names of the parameters
   * @param types        the types of the parameters
   */
  public BeanMakerImpl(Class<?> factoryClass, String factoryName, List<String> names, List<Class<?>> types) {
    this.factoryClass = factoryClass;
    this.factoryName = factoryName;
    this.names = Collections.unmodifiableList(names);
    this.types = Collections.unmodifiableList(types);
  }


  /**
   * New instance.
   *
   * @param factoryClass the class containing the factory method
   * @param factoryName  the name of the factory method
   * @param constructor  the constructor
   */
  public BeanMakerImpl(Class<?> factoryClass, String factoryName, BeanConstructor constructor) {
    this(factoryClass, factoryName, constructor.getNames(), constructor.getTypes());
  }

}
