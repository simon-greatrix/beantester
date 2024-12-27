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

    inferNullBehaviour(original.beanCreator(), holder);

    // Now the bean properties.
    holder.preferWriters(true)
        .reset()
        .setAllProperties(ValueType.PRIMARY, true);

    inferNullBehaviour(original, holder);
  }


  private static void inferNullBehaviour(Model<?> model, BeanHolder holder) {
    for (String name : model.propertyNames()) {
      Object originalValue = holder.readExpected(name);
      holder.setProperty(name, null);
      try {
        try {
          holder.bean();
        } catch (Throwable e) {
          model.property(name).nullBehaviour(NullBehaviour.ERROR);
          continue;
        }

        if (!holder.isReadable(name)) {
          // Record behaviour when property is omitted.
          model.property(name).nullBehaviour(NullBehaviour.NOT_READABLE);
          continue;
        }

        Object value = holder.readActual(name);
        if (value == null) {
          // Record behaviour when property is set to null.
          model.property(name).nullBehaviour(NullBehaviour.NULL);
        } else {
          // Record behaviour when property is set to a non-null value.
          model.property(name)
              .nullBehaviour(NullBehaviour.VALUE)
              .nullValue(value);
        }

      } finally {
        holder.setProperty(name, originalValue);
      }

    }
  }


  /**
   * Infer the omitted behaviour of a bean's creator's properties.
   *
   * @param original the bean description
   */
  public static void inferOmittedBehaviour(BeanDescription original) {
    // Create a copy so we can alter it safely.
    BeanDescription copyDescription = new BeanDescription(original);
    BeanHolder holder = copyDescription.createHolder();

    // Loop over the original properties as we won't be changing the original.
    List<Property> properties = List.copyOf(original.beanCreator().properties());
    for (Property property : properties) {
      String name = property.name();
      copyDescription.beanCreator().removeProperty(name);
      holder.reset();

      try {
        holder.bean();
      } catch (Throwable e) {
        // Record behaviour when property is omitted.
        original.beanCreator().property(name).omittedBehaviour(NullBehaviour.ERROR);
        continue;
      } finally {
        // Restore the property and continue.
        copyDescription.beanCreator().property(property);
      }

      if (!holder.isReadable(name)) {
        // Record behaviour when property is omitted.
        original.beanCreator().property(name).omittedBehaviour(NullBehaviour.NOT_READABLE);
        continue;
      }

      Object value = holder.readActual(name);
      if (value == null) {
        // Record behaviour when property is set to null.
        original.beanCreator().property(name).omittedBehaviour(NullBehaviour.NULL);
      } else {
        // Record behaviour when property is set to a non-null value.
        original.beanCreator().property(name)
            .omittedBehaviour(NullBehaviour.VALUE)
            .omittedValue(value);
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
      // A not-null property should error when set to null.
      if (property.notNull()) {
        if (property.nullBehaviour() == NullBehaviour.NULL) {
          throw new AssertionException("Class "+bean.beanClass()+" : Creator property \"" + property.name() + "\" is not-null but allows null values on creation.");
        }
      } else {
        if (property.nullBehaviour() == NullBehaviour.ERROR) {
          throw new AssertionException("Class "+bean.beanClass()+" : Creator property \"" + property.name() + "\" is nullable but throws an error when set to null on creation.");
        }
      }
    }

    for (Property property : bean.properties()) {
      // A not-null property should error when set to null.
      if (property.notNull()) {
        if (property.nullBehaviour() == NullBehaviour.NULL) {
          throw new AssertionException("Class "+bean.beanClass()+" : Bean property \"" + property.name() + "\" is not-null but allows null values.");
        }
      } else {
        if (property.nullBehaviour() == NullBehaviour.ERROR) {
          throw new AssertionException("Class "+bean.beanClass()+" : Bean property \"" + property.name() + "\" is nullable but throws an error when set to null.");
        }
      }
    }
  }

}
