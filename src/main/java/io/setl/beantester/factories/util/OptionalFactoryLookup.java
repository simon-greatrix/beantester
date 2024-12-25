package io.setl.beantester.factories.util;

import static java.util.Collections.unmodifiableMap;

import static io.setl.beantester.mirror.Executables.getRawType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.factories.NoSuchFactoryException;
import io.setl.beantester.factories.ValueFactoryRepository;


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


  private ValueFactory createOptionalPopulatingFactory(Type typeToken) {
    Class<?> rawType = getRawType(typeToken);
    return findInstanceFactory(typeToken, rawType);
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
      return new ValueFactory((t) -> Optional.ofNullable(itemValueFactory.create(t)));
    }

    if (rawType.equals(OptionalInt.class)) {
      return new ValueFactory((t) -> OptionalInt.of((Integer) itemValueFactory.create(t)));
    }

    if (rawType.equals(OptionalLong.class)) {
      return new ValueFactory((t) -> OptionalLong.of((Long) itemValueFactory.create(t)));
    }

    if (rawType.equals(OptionalDouble.class)) {
      return new ValueFactory((t) -> OptionalDouble.of((Double) itemValueFactory.create(t)));
    }

    throw new IllegalArgumentException("Unknown optional type:" + type);
  }


  private ValueFactory findItemFactory(Type itemType) {
    ValueFactoryRepository repository = TestContext.get().getFactories();
    try {
      return repository.getFactory(itemType);
    } catch (NoSuchFactoryException e) {
      return repository.getFactory(void.class);
    }
  }


  @Override
  public ValueFactory getFactory(Type typeToken) throws IllegalArgumentException, NoSuchFactoryException {
    return createOptionalPopulatingFactory(typeToken);
  }


  @Override
  public boolean hasFactory(Type type) {
    Class<?> clazz = getRawType(type);
    return !clazz.equals(void.class) && isAssignableToOptional(clazz);
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
