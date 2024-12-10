package io.setl.beantester.factories;

import static org.meanbean.util.Types.getRawType;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import java.util.random.RandomGenerator;
import org.meanbean.util.Types;

/**
 * FactoryCollection for array types
 */
public class ArrayFactoryLookup implements FactoryLookup {

  // TODO is there a way to bias so that 50% of the time matching values are produced
  // and 50% of the time, non-matching values are produced?
  private final RandomGenerator randomValueGenerator = RandomGenerator.getInstance();

  private int maxSize = 8;


  private Factory<?> getComponentFactory(Class<?> clazz) {
    FactoryCollection instance = FactoryCollection.getInstance();
    return instance.getFactory(clazz.getComponentType());
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> Factory<T> getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
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
    int length = randomValueGenerator.nextInt(maxSize);
    Factory<?> componentFactory = getComponentFactory(clazz);
    Object array = Array.newInstance(clazz.getComponentType(), length);
    for (int i = 0; i < length; i++) {
      Array.set(array, i, componentFactory.create());
    }
    return array;
  }


  public void setMaxSize(int maxArrayLength) {
    this.maxSize = maxArrayLength;
  }

}
