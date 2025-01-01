package io.setl.beantester.factories;

import java.util.function.Function;

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
   * @param <T>       the type of object to create.
   */
  public <T> ConvertingFactory(Class<T> type, ValueFactory source, Function<Object, T> converter) {
    super(type, (t) -> converter.apply(source.create(t)));
  }

}
