package io.setl.beantester;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import lombok.Getter;

import io.setl.beantester.mirror.Executables;

/**
 * A Factory that creates random objects of the specified type.
 */
public class ValueFactory {

  /** A special factory for the "java.lang.Void" class. This factory only returns nulls. */
  public static final ValueFactory VOID_CLASS_FACTORY = new ValueFactory(Void.class);

  /** A special factory for the "void" class. This factory only returns nulls. */
  public static final ValueFactory VOID_TYPE_FACTORY = new ValueFactory(void.class);

  static final ThreadLocal<LinkedList<Type>> STACK = ThreadLocal.withInitial(LinkedList::new);


  static LinkedList<Type> stack() {
    return STACK.get();
  }


  /** The standard primary value. */
  private final Supplier<Object> primary;

  /** Supplier of random values. */
  private final Supplier<Object> random;

  /** The standard secondary value. */
  private final Supplier<Object> secondary;

  /** The type of object to create. */
  @Getter
  private final Type type;


  private ValueFactory(Class<?> type) {
    this.type = type;
    primary = () -> null;
    secondary = () -> null;
    random = () -> null;
  }


  /**
   * Construct a new Factory.
   *
   * @param type      the type of object to create.
   * @param primary   The standard primary value.
   * @param secondary The standard secondary value.
   * @param random    The random value generator.
   */
  public ValueFactory(Type type, Supplier<Object> primary, Supplier<Object> secondary, Supplier<Object> random) {
    this.type = Objects.requireNonNull(type);
    this.primary = primary;
    this.secondary = secondary;
    this.random = random;

    Class<?> rawType = Executables.getRawType(type);
    if (!rawType.isInstance(Objects.requireNonNull(primary).get())) {
      throw new IllegalArgumentException("Primary value must be of the declared type: " + type);
    }

    if (!rawType.isInstance(Objects.requireNonNull(secondary).get())) {
      throw new IllegalArgumentException("Secondary value must be of the declared type: " + type);
    }

    if (!rawType.isInstance(Objects.requireNonNull(random).get())) {
      throw new IllegalArgumentException("Random value must be of the declared type: " + type);
    }
  }


  /**
   * Construct a new Factory.
   *
   * @param source of values of specified types.
   */
  public ValueFactory(Type type, Function<ValueType, Object> source) {
    this(
        type,
        () -> source.apply(ValueType.PRIMARY),
        () -> source.apply(ValueType.SECONDARY),
        () -> source.apply(ValueType.RANDOM)
    );
  }


  /**
   * Create a new object of the specified type.
   *
   * @return A new object of the specified type.
   */
  public Object create(ValueType type) {
    stack().push(this.type);
    try {
      return switch (type) {
        case PRIMARY -> getPrimary();
        case SECONDARY -> getSecondary();
        default -> getRandom();
      };
    } finally {
      stack().pop();
    }
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
    return new ValueFactory(type, datum, secondary, random);
  }


  public ValueFactory withRandom(Supplier<Object> datum) {
    return new ValueFactory(type, primary, secondary, datum);
  }


  public ValueFactory withRandom(Function<RandomGenerator, Object> datum) {
    return new ValueFactory(type, primary, secondary, () -> datum.apply(TestContext.get().getRandom()));
  }


  public ValueFactory withSecondary(Supplier<Object> datum) {
    return new ValueFactory(type, primary, datum, random);
  }

}
