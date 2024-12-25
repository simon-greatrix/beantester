package io.setl.beantester;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.ValueType;

/**
 * A Factory that creates random objects of the specified type.
 */
public class ValueFactory {

  /** The standard primary value. */
  private final Supplier<Object> primary;

  private final Supplier<Object> random;

  /** The standard secondary value. */
  private final Supplier<Object> secondary;


  /**
   * Construct a new Factory.
   *
   * @param primary   The standard primary value.
   * @param secondary The standard secondary value.
   * @param random    The random value generator.
   */
  public ValueFactory(Supplier<Object> primary, Supplier<Object> secondary, Supplier<Object> random) {
    this.primary = primary;
    this.secondary = secondary;
    this.random = random;
  }


  /**
   * Construct a new Factory.
   *
   * @param source of values of specified types.
   */
  public ValueFactory(Function<ValueType, Object> source) {
    this.primary = () -> source.apply(ValueType.PRIMARY);
    this.secondary = () -> source.apply(ValueType.SECONDARY);
    this.random = () -> source.apply(ValueType.RANDOM);
  }


  /**
   * Construct a new Factory. Any subclass using this must override the getPrimary, getSecondary and getRandom methods.
   */
  protected ValueFactory() {
    this(null, null, null);
  }


  /**
   * Create a new object of the specified type.
   *
   * @return A new object of the specified type.
   */
  public Object create(ValueType type) {
    return switch (type) {
      case PRIMARY -> getPrimary();
      case SECONDARY -> getSecondary();
      default -> getRandom();
    };
  }


  protected Object getPrimary() {
    return primary.get();
  }


  protected Object getRandom() {
    return random.get();
  }


  protected Object getSecondary() {
    return secondary.get();
  }


  public ValueFactory withPrimary(Supplier<Object> datum) {
    return new ValueFactory(datum, secondary, random);
  }


  public ValueFactory withRandom(Supplier<Object> datum) {
    return new ValueFactory(primary, secondary, datum);
  }


  public ValueFactory withRandom(Function<RandomGenerator, Object> datum) {
    return new ValueFactory(primary, secondary, () -> datum.apply(TestContext.get().getRandom()));
  }


  public ValueFactory withSecondary(Supplier<Object> datum) {
    return new ValueFactory(primary, datum, random);
  }

}
