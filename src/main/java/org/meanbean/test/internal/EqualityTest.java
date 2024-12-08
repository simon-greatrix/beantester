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

package org.meanbean.test.internal;

/**
 * Equality test of two objects.
 *
 * @author Graham Williamson
 */
public enum EqualityTest {

  /**
   * Test the logical equality of two objects (x.equals(y)).
   */
  LOGICAL {
    @Override
    public boolean test(Object x, Object y) {
      return x.equals(y);
    }
  },

  /**
   * Test the absolute equality of two objects (x == y).
   */
  ABSOLUTE {
    @Override
    public boolean test(Object x, Object y) {
      return x == y;
    }
  };


  /**
   * Is object x equal to object y.
   *
   * @param x The first object to compare.
   * @param y The second object to compare.
   *
   * @return <code>true</code> if the objects are considered equal; <code>false</code> otherwise.
   */
  public abstract boolean test(Object x, Object y);
}
