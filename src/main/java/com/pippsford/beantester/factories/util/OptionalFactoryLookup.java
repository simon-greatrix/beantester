package com.pippsford.beantester.factories.util;

import static java.util.Collections.unmodifiableMap;

import static com.pippsford.beantester.mirror.Executables.getRawType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.FactoryLookup;
import com.pippsford.beantester.factories.FactoryRepository;
import com.pippsford.beantester.factories.NoSuchFactoryException;


/**
 * FactoryCollection for Optional, OptionalInt, OptionalLong, and OptionalDouble types.
 */
public class OptionalFactoryLookup implements FactoryLookup {

  private static final Map<Class<?>, Class<?>> OPTIONAL_TO_ITEM_TYPE_MAP = createOptionalTypeMap();


  private static Map<Class<?>, Class<?>> createOptionalTypeMap() {
    Map<Class<?>, Class<?>> map = new HashMap<>();
    map.put(Optional.class, null);
    map.put(OptionalInt.class, Integer.class);
    map.put(OptionalLong.class, Long.class);
    map.put(OptionalDouble.class, Double.class);
    return unmodifiableMap(map);
  }


  private Type findFirstElementType(Type type) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments()[0];
    }
    return String.class;
  }


  private ValueFactory findInstanceFactory(Type type, Class<?> rawType) {
    Class<?> itemType = OPTIONAL_TO_ITEM_TYPE_MAP.get(rawType);
    ValueFactory itemValueFactory = itemType == null
        ? findItemFactory(findFirstElementType(type))
        : findItemFactory(itemType);

    if (rawType.equals(Optional.class)) {
      return new ValueFactory(Optional.class, (t) -> Optional.ofNullable(itemValueFactory.create(t)));
    }

    if (rawType.equals(OptionalInt.class)) {
      return new ValueFactory(OptionalInt.class, (t) -> OptionalInt.of((Integer) itemValueFactory.create(t)));
    }

    if (rawType.equals(OptionalLong.class)) {
      return new ValueFactory(OptionalLong.class, (t) -> OptionalLong.of((Long) itemValueFactory.create(t)));
    }

    if (rawType.equals(OptionalDouble.class)) {
      return new ValueFactory(OptionalDouble.class, (t) -> OptionalDouble.of((Double) itemValueFactory.create(t)));
    }

    throw new IllegalArgumentException("Unknown optional type:" + type);
  }


  private ValueFactory findItemFactory(Type itemType) {
    FactoryRepository repository = TestContext.get().getFactories();
    try {
      return repository.getFactory(itemType);
    } catch (NoSuchFactoryException e) {
      return repository.getFactory(void.class);
    }
  }


  @Override
  public Optional<ValueFactory> getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
    Class<?> clazz = getRawType(typeToken);
    if (clazz.equals(void.class) || !isAssignableToOptional(clazz)) {
      return Optional.empty();
    }

    return Optional.of(findInstanceFactory(typeToken, clazz));
  }


  private boolean isAssignableToOptional(Class<?> clazz) {
    for (Class<?> optionalType : OPTIONAL_TO_ITEM_TYPE_MAP.keySet()) {
      if (optionalType.isAssignableFrom(clazz)) {
        return true;
      }
    }
    return false;
  }

}
