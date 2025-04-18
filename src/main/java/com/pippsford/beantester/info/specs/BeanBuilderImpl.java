package com.pippsford.beantester.info.specs;

import lombok.Value;

import com.pippsford.beantester.info.Specs;
import com.pippsford.beantester.mirror.SerializableLambdas.SerializableFunction0;
import com.pippsford.beantester.mirror.SerializableLambdas.SerializableFunction1;

/** Implementation of BuilderMethods. */
@Value
public class BeanBuilderImpl implements Specs.BuilderMethods {

  /** The method invoked on the builder to create the bean. */
  SerializableFunction1<Object, Object> buildFunction;

  /** The static method invoked to create a builder. */
  SerializableFunction0<Object> builderSupplier;


  /**
   * New instance.
   *
   * @param builderSupplier the static method invoked to create a builder
   * @param buildFunction   the method invoked on the builder to create the bean
   */
  public BeanBuilderImpl(
      SerializableFunction0<Object> builderSupplier,
      SerializableFunction1<Object, Object> buildFunction
  ) {
    this.builderSupplier = builderSupplier;
    this.buildFunction = buildFunction;
  }

}
