package io.setl.beantester;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
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

  /** Stack of types currently being created to track recursion. */
  static final ThreadLocal<AtomicInteger> DEPTH = ThreadLocal.withInitial(() -> new AtomicInteger(0));


  /**
   * Get the structure depth.
   *
   * @return the current structure depth
   */
  public static int getStructureDepth() {
    return DEPTH.get().get();
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

  /** Random values for re-use. */
  private final LinkedList<Object> values = new LinkedList<>();

  /** Structure ID the random values relate to. */
  private long structureId = Long.MIN_VALUE;


  private ValueFactory(Class<?> type) {
    this.type = type;
    primary = () -> null;
    secondary = () -> null;
    random = () -> null;
  }


  /**
   * Construct a new Factory, optionally testing it.
   *
   * @param type      the type of object to create.
   * @param doTest    true to skip testing the factory.
   * @param primary   The standard primary value.
   * @param secondary The standard secondary value.
   * @param random    The random value generator.
   */
  public ValueFactory(Type type, boolean doTest, Supplier<Object> primary, Supplier<Object> secondary, Supplier<Object> random) {
    this.type = Objects.requireNonNull(type);
    this.primary = primary;
    this.secondary = secondary;
    this.random = random;

    // Ensure types are correct and can be created.
    if (doTest) {
      Class<?> rawType = Executables.getRawType(type);
      if (!rawType.isInstance(create(ValueType.PRIMARY))) {
        throw new IllegalArgumentException("Primary value must be of the declared type: " + type);
      }

      if (!rawType.isInstance(create(ValueType.SECONDARY))) {
        throw new IllegalArgumentException("Secondary value must be of the declared type: " + type);
      }

      if (!rawType.isInstance(create(ValueType.RANDOM))) {
        throw new IllegalArgumentException("Random value must be of the declared type: " + type);
      }
    }
  }


  /**
   * Construct a new Factory. The factory will be tested.
   *
   * @param type      the type of object to create.
   * @param primary   The standard primary value.
   * @param secondary The standard secondary value.
   * @param random    The random value generator.
   */
  public ValueFactory(Type type, Supplier<Object> primary, Supplier<Object> secondary, Supplier<Object> random) {
    this(type, true, primary, secondary, random);
  }


  /**
   * Construct a new Factory. The factory will be tested.
   *
   * @param source of values of specified types.
   */
  public ValueFactory(Type type, Function<ValueType, Object> source) {
    this(
        type,
        true,
        () -> source.apply(ValueType.PRIMARY),
        () -> source.apply(ValueType.SECONDARY),
        () -> source.apply(ValueType.RANDOM)
    );
  }


  /**
   * Create a new object of the specified type.
   *
   * @param valueType the type of value to create.
   *
   * @return A new object of the specified type.
   */
  public Object create(ValueType valueType) {
    int depth = DEPTH.get().getAndIncrement();
    if (depth == 0) {
      TestContext.get().beginStructure();
    }

    try {
      return switch (valueType) {
        case PRIMARY -> getPrimary();
        case SECONDARY -> getSecondary();
        default -> selectRandom();
      };
    } finally {
      DEPTH.get().decrementAndGet();
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


  private Object selectRandom() {
    if( type.getTypeName().contains("Recurse") ) {
      System.out.println("Recurse "+System.identityHashCode(this)+" : "+values.size());
    }
    long currentId = TestContext.get().getStructureId();
    if (currentId != structureId) {
      structureId = currentId;
      values.clear();
    }

    int max = TestContext.get().getMaxDistinctValues();
    if (values.size() < max || values.isEmpty()) {
      Object value = random.get();
      values.add(value);
      return value;
    }

    Object value = values.removeFirst();
    values.addLast(value);
    return value;
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
