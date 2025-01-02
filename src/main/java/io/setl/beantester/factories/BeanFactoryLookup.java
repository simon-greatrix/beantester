package io.setl.beantester.factories;

import static io.setl.beantester.mirror.Executables.getRawType;

import java.lang.System.Logger.Level;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;

/**
 * The bean factory lookup is special as it is a lookup of last resort. It will always be consulted last when looking for a factory.
 */
public class BeanFactoryLookup implements FactoryLookup {

  static class Suppliers {

    private final BeanDescription description;

    private final LinkedList<BeanHolder> holders = new LinkedList<>();


    Suppliers(BeanDescription description) {
      this.description = description;
    }


    Object create(ValueType type) {
      BeanHolder holder;
      if (holders.isEmpty()) {
        holder = description.createHolder();
      } else {
        holder = holders.removeLast();
      }
      try {
        return holder.create(type);
      } finally {
        holders.add(holder);
      }
    }

  }



  private final Map<Class<?>, ValueFactory> knownFactories = new HashMap<>();


  private Optional<ValueFactory> findFactory(Class<?> clazz, boolean logFailure) {
    ValueFactory factory = knownFactories.get(clazz);
    if (factory != null) {
      return Optional.of(factory);
    }

    try {
      BeanDescription information = BeanDescription.create(clazz);
      Suppliers suppliers = new Suppliers(information);
      factory = new ValueFactory(
          clazz,
          false,
          () -> suppliers.create(ValueType.PRIMARY),
          () -> suppliers.create(ValueType.SECONDARY),
          () -> suppliers.create(ValueType.RANDOM)
      );

      knownFactories.put(clazz, factory);

      // Verify the bean can be manipulated properly.

      // Nothing set.
      BeanHolder holder = information.createHolder();
      clazz.cast(holder.reset().newBean());

      // All types
      for (ValueType type : ValueType.values()) {
        // Type with nulls
        holder.reset().setAllProperties(type, true);
        clazz.cast(holder.newBean());
      }

      return Optional.of(factory);
    } catch (Throwable t) {
      if (logFailure) {
        System.getLogger(BeanFactoryLookup.class.getName()).log(Level.ERROR, "Failed to create factory for: " + clazz, t);
      }
      return Optional.empty();
    }

  }


  @Override
  public ValueFactory getFactory(Type type) throws IllegalArgumentException, NoSuchFactoryException {
    return findFactory(getRawType(type), true)
        .orElseThrow(() -> new NoSuchFactoryException("No factory available for: " + type));
  }


  @Override
  public boolean hasFactory(Type type) throws IllegalArgumentException {
    Class<?> clazz = getRawType(type);
    return findFactory(clazz, false).isPresent();
  }

}
