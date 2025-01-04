package io.setl.beantester.factories.bean;

import java.lang.System.Logger.Level;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.factories.FactoryLookup;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;

/**
 * The bean factory lookup is special as it is a lookup of last resort. It will always be consulted last when looking for a factory.
 */
public class BeanFactoryLookup implements FactoryLookup {


  /**
   * Convert a BeanDescription to a ValueFactory.
   *
   * @param description the description to convert
   *
   * @return the factory
   */
  public static BeanValueFactory toFactory(BeanDescription description) {
    Suppliers suppliers = new Suppliers(description);
    return new BeanValueFactory(suppliers);
  }


  private final Map<Class<?>, ValueFactory> knownFactories = new HashMap<>();


  @Override
  public Optional<ValueFactory> getFactory(Type type) {
    if (!(type instanceof Class<?> clazz)) {
      return Optional.empty();
    }
    ValueFactory factory = knownFactories.get(clazz);
    if (factory != null) {
      return Optional.of(factory);
    }

    try {
      BeanDescription information = BeanDescription.create(clazz);
      factory = toFactory(information);

      knownFactories.put(clazz, factory);

      // Verify the bean can be manipulated properly.

      // Nothing set.
      BeanHolder holder = information.createHolder();
      clazz.cast(holder.reset().newBean());

      // All types
      for (ValueType vt : ValueType.values()) {
        // Type with nulls
        holder.reset().setAllProperties(vt, true);
        clazz.cast(holder.newBean());
      }

      return Optional.of(factory);
    } catch (Throwable t) {
      System.getLogger(BeanFactoryLookup.class.getName()).log(Level.INFO, "Failed to create factory for: " + clazz, t);
      return Optional.empty();
    }

  }

}
