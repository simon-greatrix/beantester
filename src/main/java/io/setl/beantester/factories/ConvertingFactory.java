package io.setl.beantester.factories;

import java.util.function.Function;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;

/**
 * A Factory that converts the output of another factory.
 */
public class ConvertingFactory extends ValueFactory {

  /**
   * Construct a new Factory.
   *
   * @param type      the type of object to create.
   * @param source    the source factory.
   * @param converter the converter function.
   */
  public ConvertingFactory(Class<?> type, ValueFactory source, Function<Object, Object> converter) {
    super(type, (t) -> converter.apply(source.createFlat(t)));
  }


  /**
   * Construct a new Factory.
   *
   * @param type      the type of object to create.
   * @param source    the source type.
   * @param converter the converter function.
   */
  public ConvertingFactory(Class<?> type, Class<?> source, Function<Object, Object> converter) {
    this(type, TestContext.get().getFactories().getFactory(source), converter);
  }

}
