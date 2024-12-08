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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.meanbean.bean.info.BeanInformation;
import org.meanbean.bean.info.JavaBeanInformationFactory;
import org.meanbean.bean.info.PropertyInformation;
import org.meanbean.test.internal.EqualityTest;

public class BeanPropertyTesterTest {

  private static final String TEST_VALUE = "MY_TEST_VALUE";



  static class Money {

    private long value;


    @Override
    public boolean equals(Object obj) {
      throw new RuntimeException("TEST EXCEPTION");
    }


    public long getValue() {
      return value;
    }


    public void setValue(long value) {
      this.value = value;
    }

  }



  static class SimpleClass {

    private Money balance;

    private Date dateOfBirth;

    private String firstName;


    public Money getBalance() {
      return balance;
    }


    public Integer getFavouriteNumber() {
      return 17;
    }


    public String getFirstName() {
      return firstName;
    }


    public String getLastName() {
      return "CONSTANT_STRING";
    }


    public void setBalance(Money balance) {
      this.balance = balance;
    }


    public void setDateOfBirth(Date dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
    }


    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }


    public void setLastName(String lastName) {
      // do nothing
    }


    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("SimpleClass [firstName=").append(firstName).append(", dateOfBirth=").append(dateOfBirth)
          .append(", balance=").append(balance).append("]");
      return builder.toString();
    }

  }

  private final SimpleClass bean = new SimpleClass();

  private final BeanPropertyTester beanPropertyTester = new BeanPropertyTester();

  private PropertyInformation balanceProperty;

  private PropertyInformation dateOfBirthProperty;

  private PropertyInformation favouriteNumberProperty;

  private PropertyInformation firstNameProperty;

  private PropertyInformation lastNameProperty;


  @Before
  public void before() {
    BeanInformation beanInformation = new JavaBeanInformationFactory().create(bean.getClass());
    Collection<PropertyInformation> properties = beanInformation.getProperties();
    Map<String, PropertyInformation> propertyMap = new HashMap<String, PropertyInformation>();
    for (PropertyInformation property : properties) {
      propertyMap.put(property.getName(), property);
    }
    firstNameProperty = propertyMap.get("firstName");
    lastNameProperty = propertyMap.get("lastName");
    dateOfBirthProperty = propertyMap.get("dateOfBirth");
    favouriteNumberProperty = propertyMap.get("favouriteNumber");
    balanceProperty = propertyMap.get("balance");
  }


  @Test
  public void testPropertyShouldNotThrowAssertionErrorWhenAbsoluteTestPasses() throws Exception {
    beanPropertyTester.testProperty(bean, firstNameProperty, TEST_VALUE, EqualityTest.ABSOLUTE);
  }


  @Test
  public void testPropertyShouldNotThrowAssertionErrorWhenLogicalTestPasses() throws Exception {
    beanPropertyTester.testProperty(bean, firstNameProperty, TEST_VALUE, EqualityTest.LOGICAL);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testPropertyShouldPreventNullBean() throws Exception {
    beanPropertyTester.testProperty(null, firstNameProperty, TEST_VALUE, EqualityTest.LOGICAL);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testPropertyShouldPreventNullEqualityTest() throws Exception {
    beanPropertyTester.testProperty(bean, firstNameProperty, TEST_VALUE, null);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testPropertyShouldPreventNullProperty() throws Exception {
    beanPropertyTester.testProperty(bean, null, TEST_VALUE, EqualityTest.LOGICAL);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testPropertyShouldPreventNullTestValue() throws Exception {
    beanPropertyTester.testProperty(bean, firstNameProperty, null, EqualityTest.LOGICAL);
  }


  @Test
  public void testPropertyShouldThrowAssertionErrorWhenTestFails() throws Exception {
    AssertionError error = null;
    try {
      beanPropertyTester.testProperty(bean, lastNameProperty, TEST_VALUE, EqualityTest.LOGICAL);

    } catch (AssertionError e) {
      error = e;
    }
    assertThat("AssertionError was not thrown when it should have been.", error, is(not(nullValue())));
    String readMethodOutput = bean.getLastName();
    String expectedMessage =
        "Property [" + lastNameProperty.getName() + "] getter did not return test value. Expected ["
            + TEST_VALUE + "] but getter returned [" + readMethodOutput + "].";
    assertThat("Incorrect message in AssertionError.", error.getMessage(), is(expectedMessage));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testPropertyShouldThrowIllegalArgumentExceptionWhenPassedInvalidTypeOfTestValue() throws Exception {
    beanPropertyTester.testProperty(bean, firstNameProperty, new Object(), EqualityTest.LOGICAL);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testPropertyShouldThrowIllegalArgumentExceptionWhenPassedReadOnlyProperty() throws Exception {
    beanPropertyTester.testProperty(bean, favouriteNumberProperty, 125, EqualityTest.LOGICAL);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testPropertyShouldThrowIllegalArgumentExceptionWhenPassedWriteOnlyProperty() throws Exception {
    beanPropertyTester.testProperty(bean, dateOfBirthProperty, new Date(), EqualityTest.LOGICAL);
  }


  @Test(expected = BeanTestException.class)
  public void testPropertyShouldWrapExceptionsInBeanTestException() throws Exception {
    beanPropertyTester.testProperty(bean, balanceProperty, new Money(), EqualityTest.LOGICAL);
  }


  @Test
  public void typesAreCompatibleShouldReturnFalseWhenTypesAreNotCompatible() throws Exception {
    beanPropertyTester.typesAreCompatible(String.class, Long.class);
    beanPropertyTester.typesAreCompatible(String.class, long.class);
    beanPropertyTester.typesAreCompatible(SimpleClass.class, String.class);
    beanPropertyTester.typesAreCompatible(double.class, long.class);
  }


  @Test
  public void typesAreCompatibleShouldReturnTrueWhenTypesAreCompatible() throws Exception {
    beanPropertyTester.typesAreCompatible(Long.class, long.class);
    beanPropertyTester.typesAreCompatible(long.class, Long.class);
    beanPropertyTester.typesAreCompatible(double.class, Double.class);
  }

}
