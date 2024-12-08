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

package org.meanbean.test.beans;

/**
 * Extension of Bean that returns an incremental hashCode value shared by all instances of the class, starting from 1.
 * This should only be used for testing.
 *
 * @author Graham Williamson
 */
public class ClassIncrementalHashCodeBean extends Bean {

  private static int NEXT_HASH_CODE = 1;


  /**
   * Returns an incremental hashCode value starting from 1.
   *
   * @return Hash code value.
   */
  @Override
  public int hashCode() {
    return NEXT_HASH_CODE++;
  }

}
