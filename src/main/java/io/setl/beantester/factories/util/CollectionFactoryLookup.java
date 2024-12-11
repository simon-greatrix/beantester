package io.setl.beantester.factories.util;


import static io.setl.beantester.util.Types.getRawType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.random.RandomGenerator;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;
import io.setl.beantester.factories.ValueFactory;
import io.setl.beantester.factories.ValueFactoryRepository;


/**
 * FactoryCollection for java.util.Collection types
 */
public class CollectionFactoryLookup implements FactoryLookup {

  private static Map<Class<?>, ValueFactory<?>> buildDefaultCollectionFactories() {
    Map<Class<?>, ValueFactory<?>> collectionFactories = new ConcurrentHashMap<>();

    // Lists
    collectionFactories.put(List.class, ArrayList::new);

    // Maps
    collectionFactories.put(Map.class, HashMap::new);
    collectionFactories.put(ConcurrentMap.class, ConcurrentHashMap::new);
    collectionFactories.put(SortedMap.class, TreeMap::new);
    collectionFactories.put(NavigableMap.class, TreeMap::new);

    // Sets
    collectionFactories.put(Set.class, HashSet::new);
    collectionFactories.put(SortedSet.class, TreeSet::new);
    collectionFactories.put(NavigableSet.class, TreeSet::new);

    // Other
    collectionFactories.put(Collection.class, ArrayList::new);
    collectionFactories.put(Queue.class, LinkedList::new);
    collectionFactories.put(Deque.class, LinkedList::new);
    collectionFactories.put(BlockingQueue.class, LinkedBlockingQueue::new);
    collectionFactories.put(BlockingDeque.class, LinkedBlockingDeque::new);
    collectionFactories.put(TransferQueue.class, LinkedTransferQueue::new);
    return collectionFactories;
  }


  private final Map<Class<?>, ValueFactory<?>> collectionFactories = buildDefaultCollectionFactories();

  private final RandomGenerator random;

  private int maxSize = 8;

  private final TestContext context;

  public CollectionFactoryLookup(TestContext context) {
    this.context = context;
    random = context.getRandom();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private ValueFactory<?> createCollectionPopulatingFactory(Type typeToken) {
    Class<?> rawType = getRawType(typeToken);
    ValueFactory<Object> instanceValueFactory = findCollectionInstanceFactory(typeToken, rawType);

    Type itemType = findElementType(typeToken, 0);
    ValueFactory<?> itemValueFactory = findItemFactory(itemType);

    if (Map.class.isAssignableFrom(rawType)) {
      return createMapPopulatingFactory(typeToken, instanceValueFactory, itemValueFactory);

    } else {
      ValueFactory<Object> populatingValueFactory = () -> {
        Collection collection = (Collection) instanceValueFactory.create();

        int size = random.nextInt(maxSize);
        for (int idx = 0; idx < size; idx++) {
          collection.add(itemValueFactory.create());
        }
        return collection;
      };

      return populatingValueFactory;
    }
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  private ValueFactory<?> createMapPopulatingFactory(Type typeToken, ValueFactory<Object> instanceValueFactory, ValueFactory<?> itemValueFactory) {
    Type valueType = findElementType(typeToken, 1);
    ValueFactory<?> valueFactory = findItemFactory(valueType);

    ValueFactory<Object> populatingValueFactory = () -> {
      Map map = (Map) instanceValueFactory.create();

      int size = random.nextInt(maxSize);
      for (int idx = 0; idx < size; idx++) {
        map.put(itemValueFactory.create(), valueFactory.create());
      }
      return map;
    };

    return populatingValueFactory;
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> ValueFactory<T> findCollectionInstanceFactory(Type type, Class<?> rawType) {
    if (isEnumMap(type, rawType)) {
      Type keyType = findElementType(type, 0);
      return () -> (T) new EnumMap((Class) keyType);
    }

    if (isEnumSet(type, rawType)) {
      Type keyType = findElementType(type, 0);
      return () -> (T) EnumSet.noneOf((Class) keyType);
    }

    ValueFactory<?> valueFactory = collectionFactories.get(rawType);
    if (valueFactory == null) {
      valueFactory = () -> {
        try {
          return rawType.getConstructor().newInstance();
        } catch (Exception e) {
          throw new IllegalStateException("cannot create instance for " + rawType, e);
        }
      };
      collectionFactories.put(rawType, valueFactory);
    }
    return (ValueFactory<T>) valueFactory;
  }


  private Type findElementType(Type type, int index) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments()[index];
    }
    return String.class;
  }


  private ValueFactory<?> findItemFactory(Type itemType) {
    ValueFactoryRepository repository = context.getValueFactoryRepository();
    try {
      return repository.getFactory(itemType);
    } catch (NoSuchFactoryException e) {
      return repository.getFactory(void.class);
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> ValueFactory<T> getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
    return (ValueFactory<T>) createCollectionPopulatingFactory(typeToken);
  }


  public int getMaxSize() {
    return maxSize;
  }


  @Override
  public boolean hasFactory(Type type) {
    Class<?> clazz = getRawType(type);
    return !clazz.equals(void.class) && (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz));
  }


  @SuppressWarnings("rawtypes")
  private boolean isEnumMap(Type type, Class<?> rawType) {
    if (rawType.equals(EnumMap.class)) {
      return true;
    }
    Type keyType = findElementType(type, 0);
    if (rawType.equals(Map.class) && keyType instanceof Class) {
      return ((Class) keyType).isEnum();
    }
    return false;
  }


  @SuppressWarnings("rawtypes")
  private boolean isEnumSet(Type type, Class<?> rawType) {
    if (rawType.equals(EnumSet.class)) {
      return true;
    }
    Type keyType = findElementType(type, 0);
    if (rawType.equals(Set.class) && keyType instanceof Class) {
      return ((Class) keyType).isEnum();
    }
    return false;
  }


  public void setMaxSize(int maxArrayLength) {
    this.maxSize = maxArrayLength;
  }

}
