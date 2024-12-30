package io.setl.beantester.factories.util;


import static io.setl.beantester.ValueType.PRIMARY;
import static io.setl.beantester.ValueType.SECONDARY;
import static io.setl.beantester.mirror.Executables.getRawType;

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
import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.factories.NoSuchFactoryException;


/**
 * FactoryCollection for java.util.Collection types
 */
public class CollectionFactoryLookup implements FactoryLookup {

  private static Map<Class<?>, Supplier<?>> buildDefaultCollectionFactories() {
    Map<Class<?>, Supplier<?>> collectionFactories = new ConcurrentHashMap<>();

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
    collectionFactories.put(Iterable.class, ArrayList::new);
    collectionFactories.put(Collection.class, ArrayList::new);
    collectionFactories.put(Queue.class, LinkedList::new);
    collectionFactories.put(Deque.class, LinkedList::new);
    collectionFactories.put(BlockingQueue.class, LinkedBlockingQueue::new);
    collectionFactories.put(BlockingDeque.class, LinkedBlockingDeque::new);
    collectionFactories.put(TransferQueue.class, LinkedTransferQueue::new);
    return collectionFactories;
  }


  private final Map<Class<?>, Supplier<?>> collectionFactories = buildDefaultCollectionFactories();

  @Setter
  @Getter
  private int maxSize = 8;


  @SuppressWarnings({"unchecked"})
  private ValueFactory createCollectionPopulatingFactory(Type typeToken) {
    Class<?> rawType = getRawType(typeToken);
    Supplier<Object> instanceValueFactory = findCollectionInstanceFactory(typeToken, rawType);

    Type itemType = findElementType(typeToken, 0);
    ValueFactory itemValueFactory = findItemFactory(itemType);

    if (Map.class.isAssignableFrom(rawType)) {
      return createMapPopulatingFactory(typeToken, instanceValueFactory, itemValueFactory);

    } else {

      Collection<Object> primary = (Collection<Object>) instanceValueFactory.get();
      primary.add(itemValueFactory.create(PRIMARY));

      Collection<Object> secondary = (Collection<Object>) instanceValueFactory.get();
      secondary.add(itemValueFactory.create(PRIMARY));
      secondary.add(itemValueFactory.create(SECONDARY));

      Supplier<Object> random = () -> {
        Collection<Object> collection = (Collection<Object>) instanceValueFactory.get();
        int size = TestContext.get().getRandom().nextInt(maxSize);
        for (int idx = 0; idx < size; idx++) {
          collection.add(itemValueFactory.create(ValueType.RANDOM));
        }
        return collection;
      };

      return new ValueFactory(typeToken, () -> primary, () -> secondary, random);
    }
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  private ValueFactory createMapPopulatingFactory(Type typeToken, Supplier<Object> instanceValueFactory, ValueFactory keyFactory) {
    Type valueType = findElementType(typeToken, 1);
    ValueFactory valueFactory = findItemFactory(valueType);

    return new ValueFactory(
        typeToken,
        (t) -> {
          Map map = (Map) instanceValueFactory.get();

          switch (t) {
            case PRIMARY:
              map.put(keyFactory.create(PRIMARY), valueFactory.create(PRIMARY));
              break;
            case SECONDARY:
              map.put(keyFactory.create(PRIMARY), valueFactory.create(PRIMARY));
              map.put(keyFactory.create(SECONDARY), valueFactory.create(SECONDARY));
              break;
            default:
              int size = TestContext.get().getRandom().nextInt(maxSize);
              for (int idx = 0; idx < size; idx++) {
                map.put(keyFactory.create(t), valueFactory.create(t));
              }
              break;
          }
          return map;
        }
    );
  }

  @SuppressWarnings("unchecked")
  private static <E extends Enum<E>> Class<E> enumType(Type type) {
    return (Class<E>) type;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> Supplier<T> findCollectionInstanceFactory(Type type, Class<?> rawType) {
    if (isEnumMap(type, rawType)) {
      Type keyType = findElementType(type, 0);
      return () -> (T) new EnumMap((Class) keyType);
    }

    if (isEnumSet(type, rawType)) {
      Type keyType = findElementType(type, 0);
      return () -> (T) EnumSet.noneOf((Class) keyType);
    }

    Supplier<?> valueFactory = collectionFactories.computeIfAbsent(rawType, k -> () -> {
      try {
        return rawType.getConstructor().newInstance();
      } catch (Exception e) {
        throw new IllegalStateException("cannot create instance for " + rawType, e);
      }
    });
    return (Supplier<T>) valueFactory;
  }


  private Type findElementType(Type type, int index) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments()[index];
    }
    return String.class;
  }


  private ValueFactory findItemFactory(Type itemType) {
    FactoryRepository repository = TestContext.get().getFactories();
    return repository.getFactory(itemType);
  }


  @Override
  public ValueFactory getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
    return createCollectionPopulatingFactory(typeToken);
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


}
