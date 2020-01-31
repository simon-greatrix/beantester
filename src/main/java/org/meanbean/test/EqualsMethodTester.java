package org.meanbean.test;

import org.meanbean.bean.info.BeanInformationException;
import org.meanbean.bean.info.BeanInformationFactory;
import org.meanbean.bean.info.JavaBeanInformationFactory;
import org.meanbean.factories.FactoryCollection;
import org.meanbean.factories.equivalent.EquivalentEnumFactory;
import org.meanbean.factories.equivalent.EquivalentPopulatedBeanFactory;
import org.meanbean.factories.util.FactoryLookupStrategy;
import org.meanbean.lang.EquivalentFactory;
import org.meanbean.util.RandomValueGenerator;
import org.meanbean.util.SimpleValidationHelper;
import org.meanbean.util.ValidationHelper;
import org.meanbean.logging.$Logger;
import org.meanbean.logging.$LoggerFactory;

/**
 * <p>
 * Provides a means of testing the correctness of the equals logic implemented by a type, based solely on the type, with
 * respect to:
 * </p>
 * 
 * <ul>
 * <li>the general equals contract</li>
 * 
 * <li>the programmer's expectation of property significance in object equality</li>
 * </ul>
 * 
 * <p>
 * The following is tested:
 * </p>
 * 
 * <ul>
 * <li>the <strong>reflexive</strong> item of the equals contract - <code>x.equals(x)</code> should hold</li>
 * 
 * <li>the <strong>symmetric</strong> item of the equals contract - if <code>x.equals(y)</code>, then
 * <code>y.equals(x)</code> should also hold</li>
 * 
 * <li>the <strong>transitive</strong> item of the equals contract - if <code>x.equals(y)</code> and
 * <code>y.equals(z)</code>, then <code>x.equals(z)</code> should hold</li>
 * 
 * <li>the <strong>consistent</strong> item of the equals contract - if <code>x.equals(y)</code>, then
 * <code>x.equals(y)</code> should hold (remain consistent) across multiple invocations, so long as neither object
 * changes</li>
 * 
 * <li>the <strong>null</strong> item of the equals contract - a non-null object should not be deemed equal to another
 * <code>null</code> object</li>
 * 
 * <li>that an entirely different type of object is not deemed equal to an object created by the specified factory.</li>
 * 
 * <li>that the equality of an object <strong>is not</strong> affected by properties that <strong>are not</strong>
 * considered in the equality logic</li>
 * 
 * <li>that the equality of an object <strong>is</strong> affected by properties that <strong>are</strong> considered in
 * the equality logic</li>
 * </ul>
 * 
 * <p>
 * To test the equals logic implemented by a class called MyClass do the following:
 * </p>
 * 
 * <pre>
 * EqualsMethodTester tester = new EqualsMethodTester();
 * tester.testEqualsMethod(MyClass.class);
 * </pre>
 * 
 * <p>
 * In the above example all properties are assumed to be considered by MyClass's equals logic.
 * </p>
 * 
 * <p>
 * The following example tests the equals logic implemented by a class called MyComplexClass which has two properties:
 * firstName and lastName. Only firstName is considered in the equals logic. Therefore, lastName is specified in the
 * insignificantProperties varargs:
 * </p>
 * 
 * <pre>
 * EqualsMethodTester tester = new EqualsMethodTester();
 * tester.testEqualsMethod(MyComplexClass.class, &quot;lastName&quot;);
 * </pre>
 * 
 * <p>
 * In order for the above to work successfully, the class under test must have a no-argument constructor. If this is not
 * the case, you must provide an EquivalentFactory implementation:
 * </p>
 * 
 * <pre>
 * EqualsMethodTester tester = new EqualsMethodTester();
 * tester.testEqualsMethod(new EquivalentFactory&lt;MyClass&gt;() {
 * 	&#064;Override
 * 	public MyClass create() {
 * 		MyClass result = new MyClass();
 * 		// initialize result...
 * 		result.setName(&quot;TEST_NAME&quot;);
 * 		return result;
 * 	}
 * });
 * </pre>
 * 
 * <p>
 * The Factory creates <strong>new logically equivalent</strong> instances of MyClass. MyClass has overridden
 * <code>equals()</code> and <code>hashCode()</code>. In the above example, there is only one property, name, which is
 * considered by MyClass's equals logic.
 * </p>
 * 
 * <p>
 * The following example tests the equals logic implemented by a class called MyComplexClass which has two properties:
 * firstName and lastName, but lacks a no-argument constructor. Only firstName is considered in the equals logic.
 * Therefore, lastName is specified in the insignificantProperties varargs:
 * </p>
 * 
 * <pre>
 * EqualsMethodTester tester = new EqualsMethodTester();
 * tester.testEqualsMethod(new EquivalentFactory&lt;MyComplexClass&gt;() {
 * 	&#064;Override
 * 	public MyComplexClass create() {
 * 		MyComplexClass result = new MyComplexClass(&quot;TEST_FIRST_NAME&quot;, &quot;TEST_LAST_NAME&quot;);
 * 		return result;
 * 	}
 * }, &quot;lastName&quot;);
 * </pre>
 * 
 * @author Graham Williamson
 */
public class EqualsMethodTester {

	public static final int DEFAULT_TEST_ITERATIONS_PER_TYPE = 100;

	/** The number of times each type is tested, unless a custom Configuration overrides this global setting. */
	private final int iterations = DEFAULT_TEST_ITERATIONS_PER_TYPE;

	/** Logging mechanism. */
	private static final $Logger logger = $LoggerFactory.getLogger(EqualsMethodTester.class);

	/** Input validation helper. */
	private final ValidationHelper validationHelper = new SimpleValidationHelper(logger);

	/** Factory used to gather information about a given bean and store it in a BeanInformation object. */
	private final BeanInformationFactory beanInformationFactory = new JavaBeanInformationFactory();

	/** The verifier to which general contract verification is delegated. */
	private final EqualsMethodContractVerifier contractVerifier = new EqualsMethodContractVerifier();

	/** The verifier to which property significance verification is delegated. */
	private final EqualsMethodPropertySignificanceVerifier propertySignificanceVerifier =
	        new PropertyBasedEqualsMethodPropertySignificanceVerifier();

	/**
	 * <p>
	 * Test that the equals logic implemented by the type specified is correct by testing:
	 * </p>
	 * 
	 * <ul>
	 * <li>the <strong>reflexive</strong> item of the equals contract - <code>x.equals(x)</code> should hold</li>
	 * 
	 * <li>the <strong>symmetric</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>y.equals(x)</code> should also hold</li>
	 * 
	 * <li>the <strong>transitive</strong> item of the equals contract - if <code>x.equals(y)</code> and
	 * <code>y.equals(z)</code>, then <code>x.equals(z)</code> should hold</li>
	 * 
	 * <li>the <strong>consistent</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>x.equals(y)</code> should hold (remain consistent) across multiple invocations, so long as neither object
	 * changes</li>
	 * 
	 * <li>the <strong>null</strong> item of the equals contract - a non-null object should not be deemed equal to
	 * another <code>null</code> object</li>
	 * 
	 * <li>that an entirely different type of object is not deemed equal to an object of the specified type</li>
	 * 
	 * <li>that the equality of an object <strong>is not</strong> affected by properties that <strong>are not</strong>
	 * considered in the equality logic</li>
	 * 
	 * <li>that the equality of an object <strong>is</strong> affected by properties that <strong>are</strong>
	 * considered in the equality logic</li>
	 * </ul>
	 * 
	 * <p>
	 * If the test fails, an AssertionError is thrown.
	 * </p>
	 * 
	 * @param clazz
	 *            The type to test the equals logic of.
	 * @param insignificantProperties
	 *            The names of properties that are not used when deciding whether objects are logically equivalent. For
	 *            example, "lastName".
	 * 
	 * @throws IllegalArgumentException
	 *             If either the specified clazz or insignificantProperties are deemed illegal. For example, if either
	 *             is <code>null</code>. Also, if any of the specified insignificantProperties do not exist on the class
	 *             under test.
	 * @throws BeanInformationException
	 *             If a problem occurs when trying to obtain information about the type to test.
	 * @throws BeanTestException
	 *             If a problem occurs when testing the type, such as an inability to read or write a property of the
	 *             type to test.
	 * @throws AssertionError
	 *             If the test fails.
	 */
	public void testEqualsMethod(Class<?> clazz, String... insignificantProperties) throws IllegalArgumentException,
	        BeanInformationException, BeanTestException, AssertionError {
		testEqualsMethod(clazz, null, insignificantProperties);
	}

	/**
	 * <p>
	 * Test that the equals logic implemented by the type specified is correct by testing:
	 * </p>
	 * 
	 * <ul>
	 * <li>the <strong>reflexive</strong> item of the equals contract - <code>x.equals(x)</code> should hold</li>
	 * 
	 * <li>the <strong>symmetric</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>y.equals(x)</code> should also hold</li>
	 * 
	 * <li>the <strong>transitive</strong> item of the equals contract - if <code>x.equals(y)</code> and
	 * <code>y.equals(z)</code>, then <code>x.equals(z)</code> should hold</li>
	 * 
	 * <li>the <strong>consistent</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>x.equals(y)</code> should hold (remain consistent) across multiple invocations, so long as neither object
	 * changes</li>
	 * 
	 * <li>the <strong>null</strong> item of the equals contract - a non-null object should not be deemed equal to
	 * another <code>null</code> object</li>
	 * 
	 * <li>that an entirely different type of object is not deemed equal to an object of the specified type</li>
	 * 
	 * <li>that the equality of an object <strong>is not</strong> affected by properties that <strong>are not</strong>
	 * considered in the equality logic</li>
	 * 
	 * <li>that the equality of an object <strong>is</strong> affected by properties that <strong>are</strong>
	 * considered in the equality logic</li>
	 * </ul>
	 * 
	 * <p>
	 * If the test fails, an AssertionError is thrown.
	 * </p>
	 * 
	 * @param clazz
	 *            The type to test the equals logic of.
	 * @param customConfiguration
	 *            A custom Configuration to be used when testing to ignore the testing of named properties or use a
	 *            custom test data Factory when testing a named property. This Configuration is only used for this
	 *            individual test and will not be retained for future testing of this or any other type. If no custom
	 *            Configuration is required, pass <code>null</code> or use
	 *            <code>testEqualsMethod(Class<?>,String...)</code> instead.
	 * @param insignificantProperties
	 *            The names of properties that are not used when deciding whether objects are logically equivalent. For
	 *            example, "lastName".
	 * 
	 * @throws IllegalArgumentException
	 *             If either the specified clazz or insignificantProperties are deemed illegal. For example, if either
	 *             is <code>null</code>. Also, if any of the specified insignificantProperties do not exist on the class
	 *             under test.
	 * @throws BeanInformationException
	 *             If a problem occurs when trying to obtain information about the type to test.
	 * @throws BeanTestException
	 *             If a problem occurs when testing the type, such as an inability to read or write a property of the
	 *             type to test.
	 * @throws AssertionError
	 *             If the test fails.
	 */
	public void testEqualsMethod(Class<?> clazz, Configuration customConfiguration, String... insignificantProperties)
	        throws IllegalArgumentException, BeanInformationException, BeanTestException, AssertionError {
		logger.debug("testEqualsMethod: Entering with clazz=[{}], customConfiguration=[{}], insignificantProperties=[{}].", 
		        clazz, customConfiguration, insignificantProperties);
		validationHelper.ensureExists("clazz", "test equals method", clazz);
		EquivalentFactory<?> factory = createEquivalentFactory(clazz);
		testEqualsMethod(factory, customConfiguration, insignificantProperties);
		logger.debug("testEqualsMethod: Exiting - Equals is correct.");
	}

	/**
	 * <p>
	 * Test that the equals logic implemented by the type the specified factory creates is correct by testing:
	 * </p>
	 * 
	 * <ul>
	 * <li>the <strong>reflexive</strong> item of the equals contract - <code>x.equals(x)</code> should hold</li>
	 * 
	 * <li>the <strong>symmetric</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>y.equals(x)</code> should also hold</li>
	 * 
	 * <li>the <strong>transitive</strong> item of the equals contract - if <code>x.equals(y)</code> and
	 * <code>y.equals(z)</code>, then <code>x.equals(z)</code> should hold</li>
	 * 
	 * <li>the <strong>consistent</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>x.equals(y)</code> should hold (remain consistent) across multiple invocations, so long as neither object
	 * changes</li>
	 * 
	 * <li>the <strong>null</strong> item of the equals contract - a non-null object should not be deemed equal to
	 * another <code>null</code> object</li>
	 * 
	 * <li>that an entirely different type of object is not deemed equal to an object created by the specified factory.</li>
	 * 
	 * <li>that the equality of an object <strong>is not</strong> affected by properties that <strong>are not</strong>
	 * considered in the equality logic</li>
	 * 
	 * <li>that the equality of an object <strong>is</strong> affected by properties that <strong>are</strong>
	 * considered in the equality logic</li>
	 * </ul>
	 * 
	 * <p>
	 * If the test fails, an AssertionError is thrown.
	 * </p>
	 * 
	 * @param factory
	 *            An EquivalentFactory that creates non-null logically equivalent objects that will be used to test
	 *            whether the equals logic implemented by the type is correct. The factory must create logically
	 *            equivalent but different actual instances of the type upon each invocation of <code>create()</code> in
	 *            order for the test to be meaningful and correct.
	 * @param insignificantProperties
	 *            The names of properties that are not used when deciding whether objects are logically equivalent. For
	 *            example, "lastName".
	 * 
	 * @throws IllegalArgumentException
	 *             If either the specified factory or insignificantProperties are deemed illegal. For example, if either
	 *             is <code>null</code>. Also, if any of the specified insignificantProperties do not exist on the class
	 *             under test.
	 * @throws BeanInformationException
	 *             If a problem occurs when trying to obtain information about the type to test.
	 * @throws BeanTestException
	 *             If a problem occurs when testing the type, such as an inability to read or write a property of the
	 *             type to test.
	 * @throws AssertionError
	 *             If the test fails.
	 */
	public void testEqualsMethod(EquivalentFactory<?> factory, String... insignificantProperties)
	        throws IllegalArgumentException, BeanInformationException, BeanTestException, AssertionError {
		testEqualsMethod(factory, null, insignificantProperties);
	}

	/**
	 * <p>
	 * Test that the equals logic implemented by the type the specified factory creates is correct by testing:
	 * </p>
	 * 
	 * <ul>
	 * <li>the <strong>reflexive</strong> item of the equals contract - <code>x.equals(x)</code> should hold</li>
	 * 
	 * <li>the <strong>symmetric</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>y.equals(x)</code> should also hold</li>
	 * 
	 * <li>the <strong>transitive</strong> item of the equals contract - if <code>x.equals(y)</code> and
	 * <code>y.equals(z)</code>, then <code>x.equals(z)</code> should hold</li>
	 * 
	 * <li>the <strong>consistent</strong> item of the equals contract - if <code>x.equals(y)</code>, then
	 * <code>x.equals(y)</code> should hold (remain consistent) across multiple invocations, so long as neither object
	 * changes</li>
	 * 
	 * <li>the <strong>null</strong> item of the equals contract - a non-null object should not be deemed equal to
	 * another <code>null</code> object</li>
	 * 
	 * <li>that an entirely different type of object is not deemed equal to an object created by the specified factory.</li>
	 * 
	 * <li>that the equality of an object <strong>is not</strong> affected by properties that <strong>are not</strong>
	 * considered in the equality logic</li>
	 * 
	 * <li>that the equality of an object <strong>is</strong> affected by properties that <strong>are</strong>
	 * considered in the equality logic</li>
	 * </ul>
	 * 
	 * <p>
	 * If the test fails, an AssertionError is thrown.
	 * </p>
	 * 
	 * @param factory
	 *            An EquivalentFactory that creates non-null logically equivalent objects that will be used to test
	 *            whether the equals logic implemented by the type is correct. The factory must create logically
	 *            equivalent but different actual instances of the type upon each invocation of <code>create()</code> in
	 *            order for the test to be meaningful and correct.
	 * @param customConfiguration
	 *            A custom Configuration to be used when testing to ignore the testing of named properties or use a
	 *            custom test data Factory when testing a named property. This Configuration is only used for this
	 *            individual test and will not be retained for future testing of this or any other type. If no custom
	 *            Configuration is required, pass <code>null</code> or use
	 *            <code>testEqualsMethod(Factory<?>,String...)</code> instead.
	 * @param insignificantProperties
	 *            The names of properties that are not used when deciding whether objects are logically equivalent. For
	 *            example, "lastName".
	 * 
	 * @throws IllegalArgumentException
	 *             If either the specified factory or insignificantProperties are deemed illegal. For example, if either
	 *             is <code>null</code>. Also, if any of the specified insignificantProperties do not exist on the class
	 *             under test.
	 * @throws BeanInformationException
	 *             If a problem occurs when trying to obtain information about the type to test.
	 * @throws BeanTestException
	 *             If a problem occurs when testing the type, such as an inability to read or write a property of the
	 *             type to test.
	 * @throws AssertionError
	 *             If the test fails.
	 */
	public void testEqualsMethod(EquivalentFactory<?> factory, Configuration customConfiguration,
	        String... insignificantProperties) throws IllegalArgumentException, BeanInformationException,
	        BeanTestException, AssertionError {
		logger.debug("testEqualsMethod: Entering with factory=[" + factory + "], customConfiguration=["
		        + customConfiguration + "], insignificantProperties=[" + insignificantProperties + "].");
		validationHelper.ensureExists("factory", "test equals method", factory);
		validationHelper.ensureExists("insignificantProperties", "test equals method", insignificantProperties);
		contractVerifier.verifyEqualsReflexive(factory);
		contractVerifier.verifyEqualsSymmetric(factory);
		contractVerifier.verifyEqualsTransitive(factory);
		contractVerifier.verifyEqualsConsistent(factory);
		contractVerifier.verifyEqualsNull(factory);
		contractVerifier.verifyEqualsDifferentType(factory);
		// Override the standard number of iterations if need be
		int iterations = this.iterations;
		if ((customConfiguration != null) && (customConfiguration.hasIterationsOverride())) {
			iterations = customConfiguration.getIterations();
		}
		// Test property significance 'iterations' times
		for (int idx = 0; idx < iterations; idx++) {
			logger.debug("testEqualsMethod: Iteration [" + idx + "].");
			propertySignificanceVerifier.verifyEqualsMethod(factory, customConfiguration, insignificantProperties);
		}
		logger.debug("testEqualsMethod: Exiting - Equals is correct.");
	}

	/**
	 * Get a RandomValueGenerator.
	 * 
	 * @return A RandomValueGenerator.
	 */
	public RandomValueGenerator getRandomValueGenerator() {
		return propertySignificanceVerifier.getRandomValueGenerator();
	}

	/**
	 * Get the collection of test data Factories with which you can register new Factories for custom Data Types.
	 * 
	 * @return The collection of test data Factories.
	 */
	public FactoryCollection getFactoryCollection() {
		return propertySignificanceVerifier.getFactoryCollection();
	}

	/**
	 * Get the FactoryLookupStrategy, which provides a means of acquiring Factories.
	 * 
	 * @return The factory lookup strategy.
	 */
	public FactoryLookupStrategy getFactoryLookupStrategy() {
		return propertySignificanceVerifier.getFactoryLookupStrategy();
	}

	private EquivalentFactory<?> createEquivalentFactory(Class<?> clazz) {
		if (classIsAnEnum(clazz)) {
			return createEnumClassFactory(clazz);
		} else {
			return createPopulatedBeanFactory(clazz);
		}
	}

	private boolean classIsAnEnum(Class<?> clazz) {
		return clazz.isEnum();
	}

	private EquivalentEnumFactory createEnumClassFactory(Class<?> clazz) {
		return new EquivalentEnumFactory(clazz);
	}

	private EquivalentPopulatedBeanFactory createPopulatedBeanFactory(Class<?> clazz) {
		return new EquivalentPopulatedBeanFactory(beanInformationFactory.create(clazz), getFactoryLookupStrategy());
	}
}