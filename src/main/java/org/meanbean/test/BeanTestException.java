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

/**
 * An exception that may be thrown when trying to test a JavaBean using BeanTester.
 *
 * @author Graham Williamson
 */
public class BeanTestException extends RuntimeException {

  /** Unique version ID of this Serializable class. */
  private static final long serialVersionUID = 1L;


  /**
   * Construct a new Bean Test Exception with the specified message.
   *
   * @param message A human-readable String message describing the problem that occurred.
   */
  public BeanTestException(String message) {
    super(message);
  }


  /**
   * Construct a new Bean Test Exception with the specified message and cause.
   *
   * @param message A human-readable String message describing the problem that occurred.
   * @param cause   The cause of the exception.
   */
  public BeanTestException(String message, Throwable cause) {
    super(message, cause);
  }

}
