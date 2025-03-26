package com.pippsford.beantester.factories.basic;

import static com.pippsford.beantester.mirror.Executables.getRawType;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Optional;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.factories.FactoryLookup;
import com.pippsford.beantester.factories.FactoryRepository;
import com.pippsford.beantester.factories.NoSuchFactoryException;
import lombok.Getter;
import lombok.Setter;

/**
 * Factory for array types.
 */
@Setter
@Getter
public class ArrayFactoryLookup implements FactoryLookup {

  /** Maximum size of the array. */
  private int maxSize = 8;


  /** Create the array. */
  private Object createArray(ValueType t, Class<?> clazz) {
    // Primary and Secondary are single element arrays. A random could be any size.
    int length = (t != ValueType.RANDOM) ? 1 : TestContext.get().getRandom().nextInt(1 + maxSize);

    // Get the factory to populate the array
    ValueFactory componentValueFactory = getComponentFactory(clazz);

    // Create the array and populate it
    Object array = Array.newInstance(clazz.getComponentType(), length);
    for (int i = 0; i < length; i++) {
      Array.set(array, i, componentValueFactory.create(t));
    }

    // Array created
    return array;
  }


  /**
   * Get the factory to create the elements of the array
   *
   * @param clazz the member type of the array
   *
   * @return the factory to create the elements of the array
   */
  private ValueFactory getComponentFactory(Class<?> clazz) {
    FactoryRepository repository = TestContext.get().getFactories();
    return repository.getFactory(clazz.getComponentType());
  }


  /**
   * Get a factory if possible.
   *
   * @param typeToken The type created by the factory.
   *
   * @return the requested factory, or an empty optional if the factory cannot be created by this FactoryLookup.
   */
  @Override
  public Optional<ValueFactory> getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
    // Get the raw type to be created and verify it is an array.
    Class<?> clazz = getRawType(typeToken);
    if (!clazz.isArray()) {
      return Optional.empty();
    }

    // Create a new factory for the array type.
    return Optional.of(new ValueFactory(
        typeToken,
        () -> createArray(ValueType.PRIMARY, clazz),
        () -> createArray(ValueType.SECONDARY, clazz),
        () -> createArray(ValueType.RANDOM, clazz)
    ));
  }


}
