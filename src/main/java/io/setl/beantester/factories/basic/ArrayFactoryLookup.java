package io.setl.beantester.factories.basic;

import static io.setl.beantester.util.Types.getRawType;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;
import io.setl.beantester.factories.ValueFactory;
import io.setl.beantester.factories.ValueFactoryRepository;
import io.setl.beantester.util.Types;

/**
 * FactoryCollection for array types
 */
public class ArrayFactoryLookup implements FactoryLookup {

  private int maxSize = 8;

  private final TestContext testContext;


  public ArrayFactoryLookup(TestContext testContext) {
    this.testContext = testContext;
  }


  private ValueFactory<?> getComponentFactory(Class<?> clazz) {
    ValueFactoryRepository repository = testContext.getValueFactoryRepository();
    return repository.getFactory(clazz.getComponentType());
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> ValueFactory<T> getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
    return () -> (T) randomArray(typeToken);
  }


  public int getMaxSize() {
    return maxSize;
  }


  @Override
  public boolean hasFactory(Type type) {
    return getRawType(type).isArray();
  }


  private Object randomArray(Type typeToken) {
    Class<?> clazz = Types.getRawType(typeToken);
    int length = testContext.getRandom().nextInt(maxSize);
    ValueFactory<?> componentValueFactory = getComponentFactory(clazz);
    Object array = Array.newInstance(clazz.getComponentType(), length);
    for (int i = 0; i < length; i++) {
      Array.set(array, i, componentValueFactory.create());
    }
    return array;
  }


  public void setMaxSize(int maxArrayLength) {
    this.maxSize = maxArrayLength;
  }

}
