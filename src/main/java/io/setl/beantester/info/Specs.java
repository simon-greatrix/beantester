package io.setl.beantester.info;

import java.lang.System.Logger.Level;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.setl.beantester.mirror.SerializableLambdas.SerializableConsumer1;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction0;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction1;

/**
 * Specifications for creating beans.
 */
public class Specs {

  /**
   * Specifies a constructor. The bean's class must have a public constructor with matching argument types.
   */
  public interface BeanConstructor extends Spec {

    /** The constructor's parameter names. The parameter names must be unique and equal in number to the parameters. */
    List<String> names();

    /** The constructor's parameter types. The bean's class must have a public constructor with these exact parameter types. */
    List<Class<?>> types();

    /** Validate the names and types are the same size and all non null. */
    default void validate() {
      Objects.requireNonNull(names(), "names");
      Objects.requireNonNull(types(), "types");
      if (names().size() != types().size()) {
        throw new IllegalArgumentException("Names and types must be the same size");
      }
      for (int i = 0; i < names().size(); i++) {
        Objects.requireNonNull(names().get(i), "Parameter name at index " + i + " is null");
        Objects.requireNonNull(types().get(i), "Parameter type at index " + i + " is null");
      }
    }

  }



  /**
   * Specify to construct a bean using the specified function. The function takes a map of parameter names to values and should create the bean accordingly.
   */
  public interface BeanCreator<M extends Model> extends Spec, SerializableFunction1<Map<String, Object>, Object>, Model<M> {

  }



  /**
   * A method that can be used to create the builder for a bean.
   */
  public interface BuilderMethods extends Spec {

    /** The method invoked on the builder to create the bean. */
    SerializableFunction1<Object, Object> build();

    /** The static method invoked to create a builder. */
    SerializableFunction0<Object> builder();

  }



  /** Add a property. */
  public interface NewProperty extends Spec {

    /**
     * Get the property to add. Properties can be associated with the bean itself, the bean's creator, or both.
     *
     * @param forCreator true if the property is for the creator, false if for the bean
     *
     * @return the property, or empty if not applicable
     */
    Optional<Property> get(boolean forCreator);

  }



  /**
   * Specify to customise a property. During initialisation all properties will be passed to the customiser.
   */
  public interface PropertyCustomiser extends Spec, SerializableConsumer1<Property> {

  }



  /** Remove a named property. */
  public interface RemoveProperty extends Spec {

    /** Get the name of the property to remove. */
    String get();

  }



  /**
   * A marker interface for all specifications.
   */
  public interface Spec {
    // marker
  }



  record BeanConstructorImpl(List<String> names, List<Class<?>> types) implements BeanConstructor {

    BeanConstructorImpl(List<String> names, List<Class<?>> types) {
      this.names = Collections.unmodifiableList(names);
      this.types = Collections.unmodifiableList(types);
    }

  }


  /**
   * Specify to construct a bean using the public constructor with the lowest number of arguments for which the parameter names are known.
   *
   * @param beanClass the bean's class
   *
   * @return the specification
   */
  public static BeanConstructor beanConstructor(Class<?> beanClass) {
    return beanConstructorIfPossible(beanClass).orElseThrow(() -> new IllegalArgumentException("No suitable constructor found for " + beanClass));
  }


  /**
   * Specify to construct a bean using the public constructor with the specified parameter types. The parameters must alternate between names and classes.
   *
   * @param nameAndType pairs of parameter names and types
   *
   * @return the specification
   */
  public static BeanConstructor beanConstructor(Object... nameAndType) {
    List<String> names = new ArrayList<>();
    List<Class<?>> types = new ArrayList<>();

    int length = nameAndType.length;
    if (length % 2 != 0) {
      throw new IllegalArgumentException("Odd number of arguments: " + length);
    }
    for (int i = 0; i < length; i++) {
      Object o = nameAndType[i];
      if (i % 2 == 0) {
        if (o instanceof String str) {
          names.add(str);
        } else {
          throw new IllegalArgumentException("Expected a name (String) at index " + i + " but got: " + ((o != null) ? o.getClass().toString() : "null"));
        }
      } else {
        if (o instanceof Class<?> cls) {
          types.add(cls);
        } else {
          throw new IllegalArgumentException("Expected a type (Class<?>) at index " + i + " but got: " + ((o != null) ? o.getClass().toString() : "null"));
        }
      }
    }

    return new BeanConstructorImpl(names, types);
  }


  /**
   * Specify to construct a bean using the public constructor with the lowest number of arguments for which the parameter names are known.
   *
   * @param beanClass the bean's class
   *
   * @return the specification
   */
  public static Optional<BeanConstructor> beanConstructorIfPossible(Class<?> beanClass) {
    Constructor<?>[] constructors = beanClass.getConstructors();
    Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

    boolean warningLogged = false;

    for (Constructor<?> constructor : constructors) {
      boolean namesAreKnown = true;

      for (Parameter parameter : constructor.getParameters()) {
        if (!parameter.isNamePresent()) {
          if (!warningLogged) {
            System.getLogger("io.setl.beantester").log(
                Level.WARNING,
                "Parameter name not present in constructor for \"" + beanClass + "\". Remember to compile with \"-parameters\" enabled."
            );
            warningLogged = true;
          }
          namesAreKnown = false;
          break;
        }
      }

      if (namesAreKnown) {
        // found it
        List<String> names = new ArrayList<>();
        List<Class<?>> types = new ArrayList<>();

        for (Parameter parameter : constructor.getParameters()) {
          names.add(parameter.getName());
          types.add(parameter.getType());
        }

        return Optional.of(new BeanConstructorImpl(names, types));
      }
    }

    return Optional.empty();
  }


  /**
   * Create a customiser that sets properties as ignored.
   *
   * @param names the names that are ignored (i.e. those that will not be tested)
   *
   * @return the customiser
   */
  public static PropertyCustomiser ignored(Collection<String> names) {
    return propertyInformation -> {
      if (names.contains(propertyInformation.name())) {
        propertyInformation.ignored(true);
      }
    };
  }


  /**
   * Create a customiser that sets properties as ignored.
   *
   * @param names the names that are ignored (i.e. those that will not be tested)
   *
   * @return the customiser
   */
  public static PropertyCustomiser ignored(String... names) {
    return ignored(Arrays.asList(names));
  }


  /**
   * Create a customiser that sets properties as ignored except for the specified names.
   *
   * @param names the names that are not ignored (i.e. those that will be tested)
   *
   * @return the customiser
   */
  public static PropertyCustomiser ignoredExcept(Collection<String> names) {
    return propertyInformation -> {
      if (!names.contains(propertyInformation.name())) {
        propertyInformation.ignored(true);
      }
    };
  }


  public static PropertyCustomiser ignoredExcept(String... propertyNames) {
    return ignoredExcept(Arrays.asList(propertyNames));
  }


  public static NewProperty newProperty(Property property, boolean onBean) {
    return (b) -> (onBean == b) ? Optional.of(property) : Optional.empty();
  }


  /**
   * Create a customiser that sets properties as not null.
   *
   * @param names the property names
   *
   * @return the customiser
   */

  public static PropertyCustomiser notNull(Collection<String> names) {
    return propertyInformation -> {
      if (names.contains(propertyInformation.name())) {
        propertyInformation.nullable(false);
      }
    };
  }


  /**
   * Create a customiser that sets properties as not null.
   *
   * @param names the property names
   *
   * @return the customiser
   */
  public static PropertyCustomiser notNull(String... names) {
    return notNull(Arrays.asList(names));
  }


  /**
   * Create a customiser that sets properties as not significant.
   *
   * @param names the property names
   *
   * @return the customiser
   */

  public static PropertyCustomiser notSignificant(String... names) {
    return notSignificant(Arrays.asList(names));
  }


  /**
   * Create a customiser that sets properties as not significant.
   *
   * @param names the property names
   *
   * @return the customiser
   */
  public static PropertyCustomiser notSignificant(Collection<String> names) {
    return propertyInformation -> {
      if (names.contains(propertyInformation.name())) {
        propertyInformation.significant(false);
      }
    };
  }


  /**
   * Create a customiser that sets properties as nullable.
   *
   * @param names the property names
   *
   * @return the customiser
   */
  public static PropertyCustomiser nullable(String... names) {
    return nullable(Arrays.asList(names));
  }


  /**
   * Create a customiser that sets properties as nullable.
   *
   * @param names the property names
   *
   * @return the customiser
   */
  public static PropertyCustomiser nullable(Collection<String> names) {
    return propertyInformation -> {
      if (names.contains(propertyInformation.name())) {
        propertyInformation.nullable(true);
      }
    };
  }


  /**
   * Create a customiser that sets properties as significant.
   *
   * @param names the property names
   *
   * @return the customiser
   */
  public static PropertyCustomiser significant(String... names) {
    return significant(Arrays.asList(names));
  }


  /**
   * Create a customiser that sets properties as significant.
   *
   * @param names the property names
   *
   * @return the customiser
   */
  public static PropertyCustomiser significant(Collection<String> names) {
    return propertyInformation -> {
      if (names.contains(propertyInformation.name())) {
        propertyInformation.significant(true);
      }
    };
  }


  /**
   * Create a customiser that sets the explicit type for a property.
   *
   * @param name the property's name
   * @param type the property's type
   *
   * @return the customiser
   */
  public static PropertyCustomiser type(String name, Class<?> type) {
    return propertyInformation -> {
      if (propertyInformation.name().equals(name)) {
        propertyInformation.type(type);
      }
    };
  }

}
