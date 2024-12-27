package io.setl.beantester.info;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.setl.beantester.TestContext;
import io.setl.beantester.ValueType;
import io.setl.beantester.factories.FactoryRepository;


/**
 * Creator for interface beans.
 */
public class BeanProxy extends AbstractModel<BeanProxy> implements BeanCreator<BeanProxy> {

  /**
   * Interface invocation handler for the bean.
   */
  class ProxyHandler implements InvocationHandler {

    private final HashMap<String, Object> values = new HashMap<>();


    ProxyHandler(Map<String, Object> values) {
      this.values.putAll(values);
    }


    private boolean handleEquals(Object proxy, Object arg) {
      if (arg == null) {
        return false;
      }

      if (arg == proxy) {
        return true;
      }

      if (!beanClass.isInstance(arg)) {
        return false;
      }

      try {
        InvocationHandler handler = Proxy.getInvocationHandler(arg);
        if (handler instanceof ProxyHandler other) {
          return values.equals(other.values);
        }
      } catch (IllegalArgumentException e) {
        return false;
      }

      return false;
    }


    private Object handleSetter(Object proxy, Method method, Object[] args) {
      String property = writeMethods.get(method);
      Object value = args[0];

      if (value == null && properties.get(property).notNull()) {
        throw new IllegalArgumentException("Null value for " + property);
      }
      Object oldValue = values.put(property, value);

      Class<?> returnType = method.getReturnType();
      if (returnType.equals(void.class) || returnType.equals(Void.class)) {
        return null;
      }

      Class<?> argType = method.getParameterTypes()[0];
      if (argType.equals(returnType)) {
        return oldValue;
      }

      if (returnType.equals(beanClass)) {
        return proxy;
      }

      throw new IllegalArgumentException("Invalid return type for " + method + " in " + beanClass);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Optional<Object> result = invokeStandard(proxy, method, args);
      if (result.isPresent()) {
        return result.get();
      }

      // If it is a "default" method, invoke it.
      if (!Modifier.isAbstract(method.getModifiers())) {
        return invokeDefault(proxy, method, args);
      }

      if (readMethods.containsKey(method)) {
        return values.get(readMethods.get(method));
      }

      if (writeMethods.containsKey(method)) {
        return handleSetter(proxy, method, args);
      }

      throw new UnsupportedOperationException("Method not supported: " + method);
    }


    private Object invokeDefault(Object proxy, Method method, Object[] args) throws Throwable {
      return MethodHandles
          .lookup()
          .findSpecial(
              beanClass,
              method.getName(),
              MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
              beanClass
          )
          .bindTo(proxy)
          .invokeWithArguments(args);
    }


    /**
     * Invoke a method from Object.
     *
     * @param proxy  the proxy object
     * @param method the method to invoke
     * @param args   the arguments
     *
     * @return the result of the invocation, or empty if not a standard method
     */
    private Optional<Object> invokeStandard(Object proxy, Method method, Object[] args) {
      // Implementation of toString
      if (method.getName().equals("toString") && method.getParameterCount() == 0) {
        return Optional.of("BeanProxy for " + beanClass.getName());
      }

      // Implementation of hashCode
      if (method.getName().equals("hashCode") && method.getParameterCount() == 0) {
        return Optional.of(values.hashCode());
      }

      // Implementation of equals
      if (method.getName().equals("equals") && method.getParameterCount() == 1) {
        return Optional.of(handleEquals(proxy, args[0]));
      }

      return Optional.empty();
    }

  }



  private final Class<?> beanClass;

  private final Map<Method, String> readMethods = new HashMap<>();

  private final Map<Method, String> writeMethods = new HashMap<>();


  /**
   * New instance.
   *
   * @param beanClass the bean class
   */
  public BeanProxy(Class<?> beanClass) {
    this.beanClass = beanClass;
    for (Property property : new BeanDescriptionFactory(beanClass).findAllProperties()) {
      property(property);

      Optional<Method> optional = property.writeMethod();
      optional.ifPresent(method -> writeMethods.put(method, property.name()));
      optional = property.readMethod();
      optional.ifPresent(method -> readMethods.put(method, property.name()));
    }
  }


  /**
   * Copy constructor.
   *
   * @param proxy the proxy to copy
   */
  public BeanProxy(BeanProxy proxy) {
    super(proxy.properties());
    this.beanClass = proxy.beanClass;
    this.readMethods.putAll(proxy.readMethods);
    this.writeMethods.putAll(proxy.writeMethods);
  }


  @Override
  public Object apply(Map<String, Object> values) {
    // Verify no unknown properties
    for (var e : values.entrySet()) {
      Property property = property(e.getKey());
      if (property == null) {
        throw new IllegalArgumentException("Value specified for unknown property: " + e.getKey());
      }
      if (e.getValue() == null && property.notNull()) {
        throw new IllegalArgumentException("Null value for " + e.getKey());
      }
    }

    // Verify all not-null are set to null
    ArrayList<Property> notNullValues = new ArrayList<>();
    for (Property property : properties()) {
      if (property.notNull()) {
        if (values.get(property.name()) == null && values.containsKey(property.name())) {
          throw new IllegalArgumentException("Null value for " + property.name());
        }
        notNullValues.add(property);
      }
    }

    ProxyHandler handler = new ProxyHandler(values);

    // Add missing not-null values
    FactoryRepository vfr = TestContext.get().getFactories();
    for (Property property : notNullValues) {
      handler.values.put(property.name(), vfr.create(ValueType.PRIMARY, beanClass, property));
    }

    return Proxy.newProxyInstance(beanClass.getClassLoader(), new Class<?>[]{beanClass}, handler);
  }


  @Override
  public BeanProxy copy() {
    return new BeanProxy(this);
  }

}
