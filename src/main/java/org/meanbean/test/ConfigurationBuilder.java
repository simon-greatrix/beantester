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

package org.meanbean.test;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.meanbean.lang.Factory;
import org.meanbean.util.ValidationHelper;

/**
 * Builder object that makes creating Configuration objects easier. <br>
 *
 * <b>Prefer {@link BeanTesterBuilder}</b>
 *
 * @author Graham Williamson
 */
public class ConfigurationBuilder {

  /** Any properties of a type that should not be tested. Contains property names. */
  private final Set<String> ignoredProperties = ConcurrentHashMap.newKeySet();

  /** Map showing which properties are explicitly not null (false) or nullable (true). */
  private final Map<String, Boolean> nullableProperties = new ConcurrentHashMap<>();

  /**
   * Factories that should be used for specific properties, overriding standard Factory selection. Keyed by property
   * name.
   */
  private final Map<String, Factory<?>> overrideFactories = new ConcurrentHashMap<>();

  private final Set<Warning> suppressedWarnings = EnumSet.noneOf(Warning.class);

  /** The number of times a type should be tested. */
  private Integer iterations;

  /** If a property can be set in a builder and via a direct setter, which do you use? */
  private boolean preferBuilder = false;


  /**
   * Construct a new Configuration Builder.
   */
  public ConfigurationBuilder() {
  }


  /**
   * Build a Configuration.
   *
   * @return A Configuration object.
   */
  public Configuration build() {
    return new Configuration(iterations, unmodifiableSet(ignoredProperties), unmodifiableMap(overrideFactories),
        unmodifiableSet(suppressedWarnings)
    );
  }


  /**
   * Register the specified Factory as an override Factory for the specified property. This means that the specified
   * Factory will be used over the standard Factory for the property.
   *
   * @param property The name of the property.
   * @param factory  The Factory to use to override standard Factory selection for the specified property.
   *
   * @return A Configuration Builder.
   *
   * @throws IllegalArgumentException If either the property or factory parameter is deemed illegal. For example, if either is null.
   */
  public ConfigurationBuilder factory(String property, Factory<?> factory) throws IllegalArgumentException {
    ValidationHelper.ensureExists("property", "add override Factory", property);
    ValidationHelper.ensureExists("factory", "add override Factory", factory);
    overrideFactories.put(property, factory);
    return this;
  }


  /**
   * Mark the specified property as one to be disregarded/ignored during testing.
   *
   * @param property The name of the property.
   *
   * @return A Configuration Builder.
   *
   * @throws IllegalArgumentException If the property parameter is deemed illegal. For example, if it is null.
   */
  public ConfigurationBuilder ignore(String property) throws IllegalArgumentException {
    ValidationHelper.ensureExists("property", "add property to ignored properties collection", property);
    ignoredProperties.add(property);
    return this;
  }


  /**
   * Set the number of times a type should be tested.
   *
   * @param iterations The number of times a type should be tested.
   *
   * @return A Configuration Builder.
   *
   * @throws IllegalArgumentException If the iterations parameter is deemed illegal. For example, if it is less than 1.
   */
  public ConfigurationBuilder iterations(int iterations) {
    if (iterations < 1) {
      throw new IllegalArgumentException("Iterations must be at least 1.");
    }
    this.iterations = iterations;
    return this;
  }


  public ConfigurationBuilder notNull(String property) {
    ValidationHelper.ensureExists("property", "add property to not-null properties collection", property);
    nullableProperties.put(property, false);
    return this;
  }


  public ConfigurationBuilder nullable(String property) {
    ValidationHelper.ensureExists("property", "add property to nullable properties collection", property);
    nullableProperties.put(property, true);
    return this;
  }


  public ConfigurationBuilder preferBuilder(boolean preferBuilder) {
    this.preferBuilder = preferBuilder;
    return this;
  }


  /**
   * Get a human-readable String representation of this object.
   *
   * @return A human-readable String representation of this object.
   */
  @Override
  public String toString() {
    String str = "ConfigurationBuilder["
        + "iterations=" + iterations + ","
        + "preferBuilder=" + preferBuilder + ","
        + "ignored=" + new TreeSet<>(this.ignoredProperties) + ","
        + "nullable=" + new TreeMap<>(this.nullableProperties) + ","
        + "override=" + new TreeMap<>(this.overrideFactories)
        + "]";
    return str;
  }

}
