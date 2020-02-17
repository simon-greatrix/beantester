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

/**
 * Defines an object that generates random values.
 * 
 * @author Graham Williamson
 */
public interface RandomValueGenerator {

	/**
	 * Generate a random byte.
	 * 
	 * @return A randomly generated byte, which may be positive or negative.
	 */
	byte nextByte();

	/**
	 * Generate a random array of bytes.
	 * 
	 * @param size
	 *            The number of bytes to generate and return. This cannot be a negative number.
	 * 
	 * @return An array of <i>size</i> randomly generated bytes, each of which may be positive or negative.
	 * 
	 * @throws IllegalArgumentException
	 *             If the size parameter is deemed illegal. For example, if it is a negative number.
	 */
	byte[] nextBytes(int size);

	/**
	 * Generate a random int.
	 * 
	 * @return A randomly generated int, which may be positive or negative.
	 */
    int nextInt();

    int nextInt(int bound);

	/**
	 * Generate a random long.
	 * 
	 * @return A randomly generated double.
	 */
	long nextLong();

	/**
	 * Generate a random float between 0.0f (inclusive) and 1.0f (exclusive).
	 * 
	 * @return A randomly generated float.
	 */
	float nextFloat();

	/**
	 * Generate a random double between 0.0d (inclusive) and 1.0d (exclusive).
	 * 
	 * @return A randomly generated double.
	 */
	double nextDouble();

	/**
	 * Generate a random boolean.
	 * 
	 * @return A randomly generated boolean.
	 */
	boolean nextBoolean();
	

	public static ServiceDefinition<RandomValueGenerator> getServiceDefinition() {
		return new ServiceDefinition<>(RandomValueGenerator.class);
	}

	public static RandomValueGenerator getInstance() {
		return getServiceDefinition().getServiceFactory()
				.getFirst();
	}

}
