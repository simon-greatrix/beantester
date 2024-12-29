package io.setl.beantester.factories.basic;

import static io.setl.beantester.mirror.Executables.getRawType;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import lombok.Getter;
import lombok.Setter;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;
import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.mirror.Executables;

/**
 * Factory for array types.
 */
@Setter
@Getter
public class ArrayFactoryLookup implements FactoryLookup {

  private int maxSize = 8;


  private ValueFactory getComponentFactory(Class<?> clazz) {
    FactoryRepository repository = TestContext.get().getFactories();
    return repository.getFactory(clazz.getComponentType());
  }


  @Override
  public ValueFactory getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
    return new ValueFactory(
        typeToken,
        () -> randomArray(ValueType.PRIMARY, typeToken),
        () -> randomArray(ValueType.SECONDARY, typeToken),
        () -> randomArray(ValueType.RANDOM, typeToken)
    );
  }


  @Override
  public boolean hasFactory(Type type) {
    return getRawType(type).isArray();
  }


  private Object randomArray(ValueType t, Type typeToken) {
    Class<?> clazz = Executables.getRawType(typeToken);
    int length = t != ValueType.RANDOM ? 1 : TestContext.get().getRandom().nextInt(maxSize);
    ValueFactory componentValueFactory = getComponentFactory(clazz);
    Object array = Array.newInstance(clazz.getComponentType(), length);
    for (int i = 0; i < length; i++) {
      Array.set(array, i, componentValueFactory.create(t));
    }
    return array;
  }


}
