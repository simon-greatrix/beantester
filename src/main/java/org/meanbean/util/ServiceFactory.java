/*-
 * ​​​
 * meanbean
 * ⁣⁣⁣
 * Copyright (C) 2010 - 2020 the original author or authors.
 * ⁣⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ﻿﻿﻿﻿﻿
 */

package org.meanbean.util;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads service through META-INF/services mechanism, additionally providing caching and ordering behavior
 */
public class ServiceFactory<T> {

  private static final ServiceContextMap serviceContextMap = new ServiceContextMap();


  /** The context key for the current thread. When this is garbage-collected, the context is cleared. */
  public static class ContextId {
    private ContextId() {
      // only created here
    }
  }


  private static class ServiceContextMap {

    private static final ThreadLocal<WeakReference<ContextId>> currentKey = new ThreadLocal<>();

    private final WeakHashMap<ContextId, Map<String, Object>> contextMapByKeys = new WeakHashMap<>();


    public synchronized void clear() {
      contextMapByKeys.clear();
      currentKey.remove();
    }

    private synchronized ContextId createContext(boolean overwrite) {
      WeakReference<ContextId> keyRef = currentKey.get();
      ContextId key = keyRef != null ? keyRef.get() : null;
      if (key == null) {
        key = new ContextId();
        currentKey.set(new WeakReference<>(key));
      } else if( overwrite ) {
        contextMapByKeys.remove(key);
        key = new ContextId();
        currentKey.set(new WeakReference<>(key));
      }
      return key;
    }

    public synchronized ContextId createContext() {
      return createContext(true);
    }


    public ContextId createContextIfNeeded() {
      return createContext(false);
    }


    public synchronized Map<String, Object> getContextMap() {
      WeakReference<ContextId> ref = currentKey.get();
      Objects.requireNonNull(ref, "context key not set");

      ContextId key = ref.get();
      Objects.requireNonNull(key, "context key not available");
      return contextMapByKeys.computeIfAbsent(key, any -> new ConcurrentHashMap<>());
    }


    public boolean hasContext() {
      WeakReference<ContextId> ref = currentKey.get();
      return ref != null && ref.get() != null;
    }

  }


  public static void clear() {
    serviceContextMap.clear();
  }


  static <T> ServiceFactory<T> create(ServiceDefinition<T> definition) {
    List<T> services = ServiceFactory.doLoad(definition);
    return new ServiceFactory<>(services, definition);
  }


  public static ContextId createContext() {
    return serviceContextMap.createContext();
  }


  public static ContextId createContextIfNeeded() {
    return serviceContextMap.createContextIfNeeded();
  }


  private static synchronized <T> List<T> doLoad(ServiceDefinition<T> serviceDefinition) {
    ServiceLoader<T> loader = new ServiceLoader<>(
        serviceDefinition.getServiceType(),
        serviceDefinition.getConstructorTypes()
    );
    List<T> services = loader.createAll(serviceDefinition.getConstructorArgs());
    Collections.sort(services, getComparator());
    return services;
  }


  public static <T> Comparator<T> getComparator() {
    return Comparator.comparingInt(ServiceFactory::getOrder);
  }


  @SuppressWarnings("unchecked")
  static synchronized <T> ServiceFactory<T> getInstance(ServiceDefinition<T> definition) {
    String inprogressKey = "Load of " + definition.getServiceType().getName() + " already in progress";

    Map<String, Object> contextMap = serviceContextMap.getContextMap();
    if (contextMap.containsKey(inprogressKey)) {
      throw new IllegalStateException(inprogressKey);
    }

    contextMap.put(inprogressKey, inprogressKey);
    try {
      return (ServiceFactory<T>) contextMap.computeIfAbsent(
          definition.getServiceType().getName(),
          key -> ServiceFactory.create(definition)
      );
    } finally {
      contextMap.remove(inprogressKey);
    }
  }


  private static <T> int getOrder(T obj) {
    Order order = obj.getClass().getAnnotation(Order.class);
    if (order == null) {
      return Order.LOWEST_PRECEDENCE;
    }
    return order.value();
  }


  public static boolean hasContext() {
    return serviceContextMap.hasContext();
    //ServiceContextMap.currentKey.get() != null;
  }


  private final List<T> services;


  private ServiceFactory(List<T> services, ServiceDefinition<T> definition) {
    if (services.isEmpty()) {
      throw new IllegalArgumentException("cannot find services for " + definition.getServiceType());
    }
    this.services = Collections.unmodifiableList(services);
  }


  public List<T> getAll() {
    return services;
  }


  public T getFirst() {
    return getAll().get(0);
  }

}
