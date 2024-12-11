package io.setl.beantester.mirror;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

class ClassUtils {

  /** Suffix for array class names: {@code "[]"}. */
  private static final String ARRAY_SUFFIX = "[]";

  /** The inner class separator character: {@code '$'}. */
  private static final char INNER_CLASS_SEPARATOR = '$';

  /** Prefix for internal array class names: {@code "["}. */
  private static final String INTERNAL_ARRAY_PREFIX = "[";

  /** Prefix for internal non-primitive array class names: {@code "[L"}. */
  private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

  /** The package separator character: {@code '.'}. */
  private static final char PACKAGE_SEPARATOR = '.';

  /**
   * Map with common Java language class name as key and corresponding Class as value.
   * Primarily for efficient deserialization of remote invocations.
   */
  private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

  /**
   * Map with primitive type name as key and corresponding primitive
   * type as value, for example: "int" -> "int.class".
   */
  private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);

  /**
   * Map with primitive wrapper type as key and corresponding primitive
   * type as value, for example: Integer.class -> int.class.
   */
  private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);


  public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {

    Objects.requireNonNull(name, "Name must not be null");

    Class<?> clazz = resolvePrimitiveClassName(name);
    if (clazz == null) {
      clazz = commonClassCache.get(name);
    }
    if (clazz != null) {
      return clazz;
    }

    // "java.lang.String[]" style arrays
    if (name.endsWith(ARRAY_SUFFIX)) {
      String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
      Class<?> elementClass = forName(elementClassName, classLoader);
      return Array.newInstance(elementClass, 0).getClass();
    }

    // "[Ljava.lang.String;" style arrays
    if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
      String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
      Class<?> elementClass = forName(elementName, classLoader);
      return Array.newInstance(elementClass, 0).getClass();
    }

    // "[[I" or "[[Ljava.lang.String;" style arrays
    if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
      String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
      Class<?> elementClass = forName(elementName, classLoader);
      return Array.newInstance(elementClass, 0).getClass();
    }

    ClassLoader clToUse = classLoader;
    if (clToUse == null) {
      clToUse = getDefaultClassLoader();
    }
    try {
      return Class.forName(name, false, clToUse);
    } catch (ClassNotFoundException ex) {
      int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
      if (lastDotIndex != -1) {
        String innerClassName = name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR
            + name.substring(lastDotIndex + 1);
        try {
          return Class.forName(innerClassName, false, clToUse);
        } catch (ClassNotFoundException ex2) {
          // Swallow - let original exception get through
        }
      }
      throw ex;
    }
  }


  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = ClassUtils.class.getClassLoader();
      if (cl == null) {
        // getClassLoader() returning null indicates the bootstrap ClassLoader
        try {
          cl = ClassLoader.getSystemClassLoader();
        } catch (Throwable ex) {
          // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
        }
      }
    }
    return cl;
  }


  /**
   * Register the given common classes with the ClassUtils cache.
   */
  private static void registerCommonClasses(Class<?>... commonClasses) {
    for (Class<?> clazz : commonClasses) {
      commonClassCache.put(clazz.getName(), clazz);
    }
  }


  public static Class<?> resolvePrimitiveClassName(String name) {
    Class<?> result = null;
    // Most class names will be quite long, considering that they
    // SHOULD sit in a package, so a length check is worthwhile.
    if (name != null && name.length() <= 7) {
      // Could be a primitive - likely.
      result = primitiveTypeNameMap.get(name);
    }
    return result;
  }


  static {
    primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
    primitiveWrapperTypeMap.put(Byte.class, byte.class);
    primitiveWrapperTypeMap.put(Character.class, char.class);
    primitiveWrapperTypeMap.put(Double.class, double.class);
    primitiveWrapperTypeMap.put(Float.class, float.class);
    primitiveWrapperTypeMap.put(Integer.class, int.class);
    primitiveWrapperTypeMap.put(Long.class, long.class);
    primitiveWrapperTypeMap.put(Short.class, short.class);
    primitiveWrapperTypeMap.put(Void.class, void.class);

    // Map entry iteration is less expensive to initialize than forEach with lambdas
    for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
      registerCommonClasses(entry.getKey());
    }

    Set<Class<?>> primitiveTypes = new HashSet<>(32);
    primitiveTypes.addAll(primitiveWrapperTypeMap.values());
    Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class,
        double[].class, float[].class, int[].class, long[].class, short[].class
    );
    primitiveTypes.add(void.class);
    for (Class<?> primitiveType : primitiveTypes) {
      primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
    }

    registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
        Float[].class, Integer[].class, Long[].class, Short[].class
    );
    registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
        Class.class, Class[].class, Object.class, Object[].class
    );
    registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
        Error.class, StackTraceElement.class, StackTraceElement[].class
    );
    registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class,
        Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class
    );

    Class<?>[] javaLanguageInterfaceArray = {
        Serializable.class, Externalizable.class,
        Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class
    };
    registerCommonClasses(javaLanguageInterfaceArray);
  }

}
