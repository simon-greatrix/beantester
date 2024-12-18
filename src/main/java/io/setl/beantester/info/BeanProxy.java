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


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (readMethods.containsKey(method)) {
        return values.get(readMethods.get(method));
      }

      if (writeMethods.containsKey(method)) {
        values.put(writeMethods.get(method), args[0]);
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
        Object arg = args[0];
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
    for (Property property : new BeanDescriptionFactory().findAllProperties(beanClass)) {
      property(property);
      Optional<Method> optional = property.writeMethod();
      optional.ifPresent(method -> writeMethods.put(method, property.name()));
      optional = property.readMethod();
      optional.ifPresent(method -> readMethods.put(method, property.name()));
    }
  }


  @Override
  public Object apply(Map<String, Object> stringObjectMap) {
    return Proxy.newProxyInstance(beanClass.getClassLoader(), new Class<?>[]{beanClass}, new ProxyHandler());
  }

}
