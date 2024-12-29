package io.setl.beantester.info;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import io.setl.beantester.NullBehaviour;
import io.setl.beantester.info.specs.BeanConstructorFactory;
import io.setl.beantester.info.specs.BeanMakerFactory;
import io.setl.beantester.info.specs.Ignored;
import io.setl.beantester.info.specs.NotNull;
import io.setl.beantester.info.specs.OnNull;
import io.setl.beantester.info.specs.OnOmitted;
import io.setl.beantester.info.specs.Significance;
import io.setl.beantester.info.specs.TypeSetter;
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
    List<String> getNames();

    /** The constructor's parameter types. The bean's class must have a public constructor with these exact parameter types. */
    List<Class<?>> getTypes();

    /** Validate the names and types are the same size and all non null. */
    default void validate() {
      Objects.requireNonNull(getNames(), "names");
      Objects.requireNonNull(getTypes(), "types");
      if (getNames().size() != getTypes().size()) {
        throw new IllegalArgumentException("Names and types must be the same size");
      }
      for (int i = 0; i < getNames().size(); i++) {
        Objects.requireNonNull(getNames().get(i), "Parameter name at index " + i + " is null");
        Objects.requireNonNull(getTypes().get(i), "Parameter type at index " + i + " is null");
      }
    }

  }



  /** A specification that provides an explicit creator for a bean. */
  public interface BeanCreatorSpec extends Spec {

    BeanCreator<?> getCreator();

  }



  /** Use a static method to create a bean directly. The method takes all the required parameters for the bean. */
  public interface BeanMaker extends BeanConstructor {

    /** The class that contains the method. */
    Class<?> getFactoryClass();

    /** The name of the method that creates the bean. */
    String getFactoryName();

  }



  /**
   * A method that can be used to create the builder for a bean.
   */
  public interface BuilderMethods extends Spec {

    /** The method invoked on the builder to create the bean. */
    SerializableFunction1<Object, Object> getBuildFunction();

    /** The static method invoked to create a builder. */
    SerializableFunction0<Object> getBuilderSupplier();

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
  public interface PropertyCustomiser extends Spec, Consumer<Property> {

  }



  /** Remove a named property. */
  public interface RemoveProperty extends Spec {

    /** Get the name of the property to remove. */
    String get();

  }



  /**
   * A specification that is resolved just before use to a collection of other specifications.
   */
  public interface ResolvingSpec extends Spec {

    /**
     * Resolve the specification to a collection of other specifications.
     *
     * @param beanClass the bean class the specification will be applied to
     *
     * @return the resolved specifications
     */
    Collection<Spec> resolve(Class<?> beanClass);

  }



  /**
   * A marker interface for all specifications.
   */
  public interface Spec {
    // marker
  }


  /**
   * Specify to construct a bean using the public constructor with the lowest number of arguments for which the parameter names are known.
   *
   * @param beanClass the bean's class
   *
   * @return the specification
   */
  public static BeanConstructor beanConstructor(Class<?> beanClass) {
    return BeanConstructorFactory.beanConstructor(beanClass);
  }


  /**
   * Specify to construct a bean using the public constructor with the specified parameter types. The parameters must alternate between names and classes.
   *
   * @param nameAndType pairs of parameter names and types
   *
   * @return the specification
   */
  public static Spec beanConstructor(Object... nameAndType) {
    return BeanConstructorFactory.beanConstructor(nameAndType);
  }


  /**
   * Create a bean maker referencing a method in the bean class. The method must be static, have a unique name and have been compiled with the '-parameter'
   * option so the parameter names are available.
   *
   * @param factoryName the name of the factory method
   * @param nameAndType pairs of parameter names and types (or just types if parameter names are available in the byte code)
   */
  public static ResolvingSpec beanMaker(String factoryName, Object... nameAndType) {
    return (beanClass) -> List.of(beanMaker(beanClass, factoryName, nameAndType));
  }


  /**
   * Create a bean maker referencing a method in the specified class. The method must be static, have a unique name and have been compiled with the '-parameter'
   * option so the parameter names are available.
   */
  public static BeanMaker beanMaker(Class<?> factoryClass, String factoryName, Object... nameAndType) {
    return BeanMakerFactory.beanMaker(factoryClass, factoryName, nameAndType);
  }


  /**
   * Create a customiser that sets properties as ignored.
   *
   * @param names the names that are ignored (i.e. those that will not be tested)
   *
   * @return the customiser
   */
  public static PropertyCustomiser ignored(Collection<String> names) {
    return new Ignored(names, true);
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
    return new Ignored(names, false);
  }


  public static PropertyCustomiser ignoredExcept(String... propertyNames) {
    return ignoredExcept(Arrays.asList(propertyNames));
  }


  /**
   * Create a bean maker referencing a method in the bean class. The method must be static, return the bean's class, have a unique name and have been compiled
   * with the '-parameter' option so the parameter names are available.
   *
   * @param factoryName the name of the factory method in the bean class.
   */
  public static ResolvingSpec namedBeanMaker(String factoryName) {
    return (beanClass) -> List.of(namedBeanMaker(beanClass, factoryName));
  }


  /**
   * Create a bean maker referencing a method in the specified class. The method must be static, have a unique name and have been compiled with the '-parameter'
   * option so the parameter names are available.
   *
   * @param factoryClass the class that contains the factory method
   * @param factoryName  the name of the factory method in the factory class
   */
  public static BeanMaker namedBeanMaker(Class<?> factoryClass, String factoryName) {
    return BeanMakerFactory.namedBeanMaker(factoryClass, factoryName);
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
    return new NotNull(names, true);
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
    return new Significance(names, false);
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
    return new NotNull(names, false);
  }


  /** Specify what properties do when they are set to null. */
  public static PropertyCustomiser onNull(NullBehaviour behaviour, Collection<String> names) {
    HashMap<String, NullBehaviour> map = new HashMap<>();
    for (String n : names) {
      map.put(n, behaviour);
    }
    return new OnNull(map);
  }


  /** Specify what properties do when they are set to null. */
  public static PropertyCustomiser onNull(NullBehaviour behaviour, String... names) {
    HashMap<String, NullBehaviour> map = new HashMap<>();
    for (String n : names) {
      map.put(n, behaviour);
    }
    return new OnNull(map);
  }


  /** Specify what properties do when they are set to null. */
  public static PropertyCustomiser onNull(Map<String, NullBehaviour> behaviour) {
    return new OnNull(Map.copyOf(behaviour));
  }


  /** Specify what properties do when they are omitted. */
  public static PropertyCustomiser onOmitted(Map<String, NullBehaviour> behaviour) {
    return new OnOmitted(behaviour);
  }


  /** Specify what properties do when they are omitted. */
  public static PropertyCustomiser onOmitted(NullBehaviour behaviour, Collection<String> names) {
    HashMap<String, NullBehaviour> map = new HashMap<>();
    for (String n : names) {
      map.put(n, behaviour);
    }
    return new OnOmitted(map);
  }


  /** Specify what properties do when they are omitted. */
  public static PropertyCustomiser onOmitted(NullBehaviour behaviour, String... names) {
    HashMap<String, NullBehaviour> map = new HashMap<>();
    for (String n : names) {
      map.put(n, behaviour);
    }
    return new OnOmitted(map);
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
    return new Significance(names, true);
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
    return new TypeSetter(name, type);
  }

}
