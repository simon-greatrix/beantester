package io.setl.beantester.test;

import java.util.Collection;
import java.util.List;

import io.setl.beantester.NullBehaviour;
import io.setl.beantester.ValueType;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;
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
    BeanHolder holder = original.createHolder();
    holder.setAllProperties(ValueType.PRIMARY, false);

    Collection<String> propertyNames = holder.getPropertyNames();
    for (String name : propertyNames) {
      Object originalValue = holder.readExpected(name);
      holder.setProperty(name, null);
      try {
        holder.bean();
      } catch (Throwable e) {
        original.beanCreator().property(name).nullBehaviour(NullBehaviour.ERROR);
        continue;
      } finally {
        holder.setProperty(name, originalValue);
      }

      // TODO - refactor this so no duplicated code
      if (!holder.isReadable(name)) {
        // Record behaviour when property is omitted.
        original.beanCreator().property(name).omittedBehaviour(NullBehaviour.NOT_READABLE);
        continue;
      }

      Object value = holder.readActual(name);
      if (value == null) {
        // Record behaviour when property is set to null.
        original.beanCreator().property(name).nullBehaviour(NullBehaviour.NULL);
      } else {
        // Record behaviour when property is set to a non-null value.
        original.beanCreator().property(name).nullBehaviour(NullBehaviour.VALUE);
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
        original.beanCreator().property(name).nullBehaviour(NullBehaviour.NULL);
      } else {
        // Record behaviour when property is set to a non-null value.
        original.beanCreator().property(name).nullBehaviour(NullBehaviour.VALUE);
      }
    }
  }

}
