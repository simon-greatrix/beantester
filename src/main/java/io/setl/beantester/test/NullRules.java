package io.setl.beantester.test;

import java.util.List;

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
        .preferWriters(false)
        .reset()
        .setAllProperties(ValueType.PRIMARY, false);

    inferNullBehaviour(original.beanCreator(), "Creator", holder);

    // Now the bean properties.
    holder.preferWriters(true)
        .reset()
        .setAllProperties(ValueType.PRIMARY, true);

    inferNullBehaviour(original, "Bean", holder);
  }


  private static void inferNullBehaviour(Model<?> model, String type, BeanHolder holder) {
    for (Property modelProperty : model.properties()) {
      if (modelProperty.ignored()) {
        continue;
      }

      NullBehaviour current = modelProperty.nullBehaviour();

      // record the original value so we can restore it.
      String name = modelProperty.name();
      Object originalValue = holder.readExpected(name);

      // Set the property to null and see what happens.
      holder.setProperty(name, null);
      try {
        try {
          holder.bean();
        } catch (Throwable e) {
          check(holder.getBeanClass(), type, name, "set to null", current, NullBehaviour.ERROR);
          modelProperty.nullBehaviour(NullBehaviour.ERROR);
          continue;
        }

        // Check if the property is readable, if not we cannot check any further.
        if (!holder.isReadable(name)) {
          // Record behaviour when property is omitted.
          modelProperty.nullBehaviour(NullBehaviour.NOT_READABLE);
          continue;
        }

        // Read the property and see what happened.
        Object value = holder.readActual(name);
        if (value == null) {
          // Property was set to null.
          check(holder.getBeanClass(), type, name, "set to null", current, NullBehaviour.NULL);
          modelProperty.nullBehaviour(NullBehaviour.NULL);
        } else {
          // Property was set to a non-null value.
          check(holder.getBeanClass(), type, name, "set to null", current, NullBehaviour.VALUE);
          modelProperty
              .nullBehaviour(NullBehaviour.VALUE)
              .nullValue(value);
        }

      } finally {
        // restore the original value.
        holder.setProperty(name, originalValue);
      }

    }
  }


  private static void inferOmittedBeanBehaviour(BeanDescription original) {
    BeanHolder holder = original.createHolder();
    for (Property property : original.properties()) {
      String name = property.name();
      if (property.ignored() || holder.hasExpected(name)) {
        // Either ignore or has an expected value, so was not omitted on bean creation.
        continue;
      }

      // If the property is not readable then we cannot check further.
      if (!property.readable()) {
        property.omittedBehaviour(NullBehaviour.NOT_READABLE);
        continue;
      }

      NullBehaviour current = property.omittedBehaviour();
      Object value = holder.readActual(name);
      if (value == null) {
        // Record behaviour when property is set to omitted and becomes null.
        check(holder.getBeanClass(), "Bean", name, "omitted", current, NullBehaviour.NULL);
        property.omittedBehaviour(NullBehaviour.NULL);
      } else {
        // Record behaviour when property is omitted and takes a non-null value.
        check(holder.getBeanClass(), "Bean", name, "omitted", current, NullBehaviour.VALUE);
        property.omittedBehaviour(NullBehaviour.VALUE)
            .omittedValue(value);
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
    BeanHolder holder = copyDescription.createHolder().preferWriters(false);

    // Loop over the original properties as we won't be changing the original.
    List<Property> properties = List.copyOf(original.beanCreator().properties());
    for (Property property : properties) {
      if (property.ignored()) {
        continue;
      }
      NullBehaviour current = property.omittedBehaviour();

      String name = property.name();
      copyDescription.beanCreator().removeProperty(name);
      holder.reset();

      try {
        holder.bean();
      } catch (Throwable e) {
        // Record behaviour when property is omitted.
        check(holder.getBeanClass(), "Creator", name, "omitted", current, NullBehaviour.ERROR);
        original.beanCreator().property(name).omittedBehaviour(NullBehaviour.ERROR);
        continue;
      } finally {
        // Restore the property and continue.
        copyDescription.beanCreator().property(property);
      }

      // If property is not readable, we cannot check further.
      if (!holder.isReadable(name)) {
        original.beanCreator().property(name).omittedBehaviour(NullBehaviour.NOT_READABLE);
        continue;
      }

      Object value = holder.readActual(name);
      if (value == null) {
        // Record behaviour when property is set to omitted and becomes null.
        check(holder.getBeanClass(), "Creator", name, "omitted", current, NullBehaviour.NULL);
        original.beanCreator().property(name).omittedBehaviour(NullBehaviour.NULL);
      } else {
        // Record behaviour when property is omitted and takes a non-null value.
        check(holder.getBeanClass(), "Creator", name, "omitted", current, NullBehaviour.VALUE);
        original.beanCreator().property(name)
            .omittedBehaviour(NullBehaviour.VALUE)
            .omittedValue(value);
      }
    }
  }


  private static void validate(Class<?> beanClass, Property property, String type) {
    if (property.ignored()) {
      return;
    }

    if (property.notNull()) {
      // not-null so not allowed to be null.
      if (property.nullBehaviour() == NullBehaviour.NULL) {
        throw new AssertionException(
            "Class " + beanClass + " : " + type + " property \"" + property.name() + "\" is not-null but allows null values.");
      }
    } else {
      // nullable so not allowed to error on null
      if (property.nullBehaviour() == NullBehaviour.ERROR) {
        throw new AssertionException(
            "Class " + beanClass + " : " + type + " property \"" + property.name() + "\" is nullable but throws an error when set to null.");
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
    for (Property property : bean.beanCreator().properties()) {
      validate(bean.beanClass(), property, "Creator");
    }

    for (Property property : bean.properties()) {
      validate(bean.beanClass(), property, "Bean");
    }
  }

}
