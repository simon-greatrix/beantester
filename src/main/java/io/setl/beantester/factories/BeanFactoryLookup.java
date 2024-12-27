package io.setl.beantester.factories;

import static io.setl.beantester.mirror.Executables.getRawType;

import java.lang.System.Logger.Level;
import java.lang.reflect.Type;
import java.util.Optional;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;

/**
 * The bean factory lookup is special as it is a lookup of last resort. It will always be consulted last when looking for a factory.
 *
 * <p>Any bean created by this factory will have nulls in all the nullable fields. This is to prevent the bean factory from getting stuck in an infinite loop
 * where BeanA has a field of type BeanB and BeanB has a field of type BeanA. In such cases it must be possible to create one of them with a null.</p>
 */
public class BeanFactoryLookup implements FactoryLookup {


  private Optional<ValueFactory> findFactory(Class<?> clazz, boolean logFailure) {
    try {
      BeanDescription information = BeanDescription.create(clazz);
      final BeanHolder holder = information.createHolder();

      // Verify the bean can be manipulated properly.
      holder.bean();

      for (ValueType type : ValueType.values()) {
        holder.setAllProperties(type, true);
        holder.bean();
      }

      return Optional.of(new ValueFactory(
          clazz,
          (type) -> {
            // See the class description for why we use nulls.
            holder.setAllProperties(type, true);
            return holder.bean();
          }
      ));
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
