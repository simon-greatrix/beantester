package io.setl.beantester.info;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      if (readMethods.containsKey(method)) {
        return values.get(readMethods.get(method));
      }

      if (writeMethods.containsKey(method)) {
        String property = writeMethods.get(method);
        Object value = args[0];
        if (value == null && !properties.get(property).nullable()) {
          throw new IllegalArgumentException("Null value for " + property);
        }
        values.put(property, value);

        // TODO - fixme the return value is not correct - could be "this" or old value.
        return null;
      }

      // Implementation of toString
      if (method.getName().equals("toString") && method.getParameterCount() == 0) {
        return "BeanProxy for " + beanClass.getName();
      }

      // Implementation of hashCode
      if (method.getName().equals("hashCode") && method.getParameterCount() == 0) {
        return values.hashCode();
      }

      // Implementation of equals
      if (method.getName().equals("equals") && method.getParameterCount() == 1) {
        return handleEquals(proxy, args[0]);
      }

      // Not a supported method
      throw new UnsupportedOperationException("Method not supported: " + method);
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
      if (e.getValue() == null && !property.nullable()) {
        throw new IllegalArgumentException("Null value for " + e.getKey());
      }
    }

    // Verify all not-null are set
    for (Property property : properties()) {
      if (!(property.nullable() || values.get(property.name()) == null)) {
        throw new IllegalArgumentException("Missing value for " + property.name());
      }
    }

    return Proxy.newProxyInstance(beanClass.getClassLoader(), new Class<?>[]{beanClass}, new ProxyHandler(values));
  }


  @Override
  public BeanProxy copy() {
    return new BeanProxy(this);
  }

}
