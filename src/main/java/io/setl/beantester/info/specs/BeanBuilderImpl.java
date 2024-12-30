package io.setl.beantester.info.specs;

import lombok.Value;

import io.setl.beantester.info.Specs;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction0;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;

@Value
public class BeanBuilderImpl implements Specs.BuilderMethods {

  /** The method invoked on the builder to create the bean. */
  SerializableFunction1<Object, Object> buildFunction;

  /** The static method invoked to create a builder. */
  SerializableFunction0<Object> builderSupplier;


  public BeanBuilderImpl(
      SerializableFunction0<Object> builderSupplier,
      SerializableFunction1<Object, Object> buildFunction
  ) {
    this.builderSupplier = builderSupplier;
    this.buildFunction = buildFunction;
  }

}
