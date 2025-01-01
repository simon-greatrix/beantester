package io.setl.beantester.test;

import java.util.List;
import java.util.Set;

import io.setl.beantester.AssertionException;
import io.setl.beantester.NullBehaviour;
import io.setl.beantester.ValueType;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.info.Model;
import io.setl.beantester.info.Property;

/**
 * Check what happens if a property is set to null or omitted from a builder.
 */
public class NullRules {

  private static void check(Class<?> clazz, String type, String name, String op, NullBehaviour expected, NullBehaviour actual) {
    if (expected != null && actual != expected) {
      throw new AssertionException("Class " + clazz + " : " + type + " property \"" + name + "\" has behaviour when " + op
          + " of " + actual + " but behaved as " + expected);
    }
  }


  /**
   * Infer the null behaviour of a bean's writable properties.
   *
   * @param original the bean description
   */
  public static void inferNullBehaviour(BeanDescription original) {
    // Run over the creator properties first.
    BeanHolder holder = original.createHolder()
        .setPreferWriters(false)
        .reset()
        .setAllProperties(ValueType.PRIMARY, false);

    inferNullBehaviour(original.getBeanCreator(), "Creator", holder);

    // Now the bean properties.
    holder.setPreferWriters(true)
        .reset()
        .setAllProperties(ValueType.PRIMARY, true);

    inferNullBehaviour(original, "Bean", holder);
  }


  private static void inferNullBehaviour(Model<?> model, String type, BeanHolder holder) {
    for (Property modelProperty : model.getProperties()) {
      if (modelProperty.isIgnored()) {
        continue;
      }

      NullBehaviour current = modelProperty.getNullBehaviour();

      // record the original value so we can restore it.
      String name = modelProperty.getName();
      Object originalValue = holder.readExpected(name);

      // Set the property to null and see what happens.
      holder.setProperty(name, null);
      try {
        try {
          holder.newBean();
        } catch (Throwable e) {
          check(holder.getBeanClass(), type, name, "set to null", current, NullBehaviour.ERROR);
          modelProperty.setNullBehaviour(NullBehaviour.ERROR)
              .setNullValue(e);
          continue;
        }

        // Check if the property is readable, if not we cannot check any further.
        if (!holder.isReadable(name)) {
          // Record behaviour when property is omitted.
          modelProperty.setNullBehaviour(NullBehaviour.NOT_READABLE);
          continue;
        }

        // Read the property and see what happened.
        Object value = holder.readActual(name);
        if (value == null) {
          // Property was set to null.
          check(holder.getBeanClass(), type, name, "set to null", current, NullBehaviour.NULL);
          modelProperty.setNullBehaviour(NullBehaviour.NULL);
        } else {
          // Property was set to a non-null value.
          check(holder.getBeanClass(), type, name, "set to null", current, NullBehaviour.VALUE);
          modelProperty
              .setNullBehaviour(NullBehaviour.VALUE)
              .setNullValue(value);
        }

      } finally {
        // restore the original value.
        holder.setProperty(name, originalValue);
      }

    }
  }


  private static void inferOmittedBeanBehaviour(BeanDescription original) {
    BeanHolder holder = original.createHolder();
    for (Property property : original.getProperties()) {
      String name = property.getName();
      if (property.isIgnored() || holder.hasExpected(name)) {
        // Either ignore or has an expected value, so was not omitted on bean creation.
        continue;
      }

      // If the property is not readable then we cannot check further.
      if (!property.isReadable()) {
        property.setOmittedBehaviour(NullBehaviour.NOT_READABLE);
        continue;
      }

      NullBehaviour current = property.getOmittedBehaviour();
      Object value = holder.readActual(name);
      if (value == null) {
        // Record behaviour when property is set to omitted and becomes null.
        check(holder.getBeanClass(), "Bean", name, "omitted", current, NullBehaviour.NULL);
        property.setOmittedBehaviour(NullBehaviour.NULL);
      } else {
        // Record behaviour when property is omitted and takes a non-null value.
        check(holder.getBeanClass(), "Bean", name, "omitted", current, NullBehaviour.VALUE);
        property.setOmittedBehaviour(NullBehaviour.VALUE)
            .setOmittedValue(value);
      }
    }
  }


  /**
   * Infer the omitted behaviour of a bean's creator's properties.
   *
   * @param original the bean description
   */
  public static void inferOmittedBehaviour(BeanDescription original) {
    inferOmittedCreationBehaviour(original);
    inferOmittedBeanBehaviour(original);
  }


  private static void inferOmittedCreationBehaviour(BeanDescription original) {
    // Create a copy so we can alter it safely.
    BeanDescription copyDescription = new BeanDescription(original);
    BeanHolder holder = copyDescription.createHolder().setPreferWriters(false);

    // Loop over the original properties as we won't be changing the original.
    Set<String> propertyNames = copyDescription.getBeanCreator().getPropertyNames();
    for (String name : propertyNames) {
      Property property = copyDescription.getBeanCreator().getProperty(name);
      if (property.isIgnored()) {
        continue;
      }
      NullBehaviour current = property.getOmittedBehaviour();

      copyDescription.getBeanCreator().removeProperty(name);
      holder.reset();

      try {
        holder.newBean();
      } catch (Throwable e) {
        // Record behaviour when property is omitted.
        check(holder.getBeanClass(), "Creator", name, "omitted", current, NullBehaviour.ERROR);
        original.getBeanCreator().getProperty(name)
            .setOmittedBehaviour(NullBehaviour.ERROR)
            .setOmittedValue(e);
        continue;
      } finally {
        // Restore the property and continue.
        copyDescription.getBeanCreator().setProperty(property);
      }

      // If property is not readable, we cannot check further.
      if (!holder.isReadable(name)) {
        original.getBeanCreator().getProperty(name).setOmittedBehaviour(NullBehaviour.NOT_READABLE);
        continue;
      }

      Object value = holder.readActual(name);
      if (value == null) {
        // Record behaviour when property is set to omitted and becomes null.
        check(holder.getBeanClass(), "Creator", name, "omitted", current, NullBehaviour.NULL);
        original.getBeanCreator().getProperty(name).setOmittedBehaviour(NullBehaviour.NULL);
      } else {
        // Record behaviour when property is omitted and takes a non-null value.
        check(holder.getBeanClass(), "Creator", name, "omitted", current, NullBehaviour.VALUE);
        original.getBeanCreator().getProperty(name)
            .setOmittedBehaviour(NullBehaviour.VALUE)
            .setOmittedValue(value);
      }
    }
  }


  private static void validate(Class<?> beanClass, Property property, String type) {
    if (property.isIgnored()) {
      return;
    }

    if (property.isNotNull()) {
      // not-null so not allowed to be null.
      if (property.getNullBehaviour() == NullBehaviour.NULL) {
        throw new AssertionException(
            "Class " + beanClass + " : " + type + " property \"" + property.getName() + "\" is not-null but allows null values.");
      }
    } else {
      // nullable so not allowed to error on null
      if (property.getNullBehaviour() == NullBehaviour.ERROR) {
        Object v = property.getNullValue();
        Throwable thrown = v instanceof Throwable ? (Throwable) v : null;
        throw new AssertionException(
            "Class " + beanClass + " : " + type + " property \"" + property.getName() + "\" is nullable but throws an error when set to null.",
            thrown
        );
      }
    }

  }


  /**
   * Validate the behaviour of a bean when properties are set to null or omitted.
   *
   * <p>A nullable property should not error when set to null</p>
   *
   * @param bean the bean description
   */
  public static void validate(BeanDescription bean) {
    for (Property property : bean.getBeanCreator().getProperties()) {
      validate(bean.getBeanClass(), property, "Creator");
    }

    for (Property property : bean.getProperties()) {
      validate(bean.getBeanClass(), property, "Bean");
    }
  }

}
