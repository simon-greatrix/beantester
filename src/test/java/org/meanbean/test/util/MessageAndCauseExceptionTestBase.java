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

package org.meanbean.test.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/**
 * Test class that tests that Exceptions that have (1) a message, and (2) a message and cause, function correctly.
 * <p>
 * Class should be extended and factory methods implemented to test specific Exceptions.
 *
 * @author Graham Williamson
 */
public abstract class MessageAndCauseExceptionTestBase {

  /**
   * Create a new instance of an Exception that takes the specified message and cause as arguments to its constructor.
   *
   * @param message The message the Exception should have.
   * @param cause   The cause of the Exception.
   *
   * @return A new instance of an Exception with the specified message and cause.
   */
  public abstract Exception createMessageAndCauseException(String message, Throwable cause);


  /**
   * Create a new instance of an Exception that takes the specified message as an argument to its constructor.
   *
   * @param message The message the Exception should have.
   *
   * @return A new instance of an Exception with the specified message.
   */
  public abstract Exception createMessageException(String message);


  @Test
  public void shouldHaveMessage() throws Exception {
    // Given
    final String expectedMessage = "TEST_MESSAGE";
    // When
    Exception exception = createMessageException(expectedMessage);
    // Then
    assertThat("Message was not set on exception.", exception.getMessage(), is(expectedMessage));
  }


  @Test
  public void shouldHaveMessageAndCause() throws Exception {
    // Given
    final String expectedMessage = "TEST_MESSAGE";
    final Throwable expectedCause = new IllegalArgumentException("ILLEGAL ARGUMENT EXCEPTION MESSAGE");
    // When
    Exception exception = createMessageAndCauseException(expectedMessage, expectedCause);
    // Then
    assertThat("Unexpected message in exception.", exception.getMessage(), is(expectedMessage));
    assertThat("Unexpected cause in exception.", exception.getCause(), is(expectedCause));
  }

}
