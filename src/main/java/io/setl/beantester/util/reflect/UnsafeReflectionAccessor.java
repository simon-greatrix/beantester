package io.setl.beantester.util.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * An implementation of {@link ReflectionAccessor} based on {@link Unsafe}.
 * <p>
 * NOTE: This implementation is designed for Java 9. Although it should work with earlier Java releases, it is better to
 * use {@link PreJava9ReflectionAccessor} for them.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
final class UnsafeReflectionAccessor extends ReflectionAccessor {

  private static Class unsafeClass;


  private static Field getOverrideField() {
    try {
      return AccessibleObject.class.getDeclaredField("override");
    } catch (NoSuchFieldException e) {
      return null;
    }
  }


  private static Object getUnsafeInstance() {
    try {
      unsafeClass = Class.forName("sun.misc.Unsafe");
      Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      return unsafeField.get(null);
    } catch (Exception e) {
      return null;
    }
  }


  private final Field overrideField = getOverrideField();

  private final Object theUnsafe = getUnsafeInstance();


  /** {@inheritDoc} */
  @Override
  public void makeAccessible(AccessibleObject ao) {
    boolean success = makeAccessibleWithUnsafe(ao);
    if (!success) {
      try {
        // unsafe couldn't be found, so try using accessible anyway
        ao.setAccessible(true);
      } catch (SecurityException e) {
        throw new IllegalStateException("sun.misc.Unsafe not found. Make fields accessible, or include sun.misc.Unsafe.", e);
      }
    }
  }


  // Visible for testing only
  boolean makeAccessibleWithUnsafe(AccessibleObject ao) {
    if (theUnsafe != null && overrideField != null) {
      try {
        Method method = unsafeClass.getMethod("objectFieldOffset", Field.class);
        long overrideOffset = (Long) method.invoke(theUnsafe, overrideField);  // long overrideOffset = theUnsafe.objectFieldOffset(overrideField);
        Method putBooleanMethod = unsafeClass.getMethod("putBoolean", Object.class, long.class, boolean.class);
        putBooleanMethod.invoke(theUnsafe, ao, overrideOffset, true); // theUnsafe.putBoolean(ao, overrideOffset, true);
        return true;
      } catch (Exception ignored) { // do nothing
      }
    }
    return false;
  }

}
