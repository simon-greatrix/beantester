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

package org.meanbean.bean.info;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JavaBeanInformationTest {

  private static final List<String> EXPECTED_PROPERTIES = Arrays
      .asList("age", "dateOfBirth", "firstName", "id", "lastName");



  static class BeanWithNoProperties {
    // Nothing
  }



  static class BeanWithProperties {

    private static long nextId = 1;

    private final long id; // readable

    private Date dateOfBirth; // writable

    private String favouriteColour;

    private String firstName; // readable-writable

    private String firstPet;

    private String lastName; // readable-writable


    public BeanWithProperties() {
      id = nextId++;
    }


    public int getAge() {
      Calendar calendar = Calendar.getInstance();
      int currentYear = calendar.get(Calendar.YEAR);
      calendar.setTime(dateOfBirth);
      int birthYear = calendar.get(Calendar.YEAR);
      return currentYear - birthYear;
    }


    protected String getFavouriteColour() {
      return favouriteColour;
    }


    public String getFirstName() {
      return firstName;
    }


    String getFirstPet() {
      return firstPet;
    }


    public long getId() {
      return id;
    }


    public String getLastName() {
      return lastName;
    }


    public void setDateOfBirth(Date dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
    }


    protected void setFavouriteColour(String favouriteColour) {
      this.favouriteColour = favouriteColour;
    }


    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }


    void setFirstPet(String firstPet) {
      this.firstPet = firstPet;
    }


    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

  }


  private Map<String, PropertyInformation> convertToMapOfPropertyNamesToPropertyInformation(
      JavaBeanInformation javaBeanInformation
  ) {
    Map<String, PropertyInformation> propertyMap = new HashMap<String, PropertyInformation>();
    for (PropertyInformation property : javaBeanInformation.getProperties()) {
      propertyMap.put(property.getName(), property);
    }
    return propertyMap;
  }


  @Test
  public void shouldHaveAgePropertyInformationOfClassPassedToConstructor() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithProperties.class);
    Map<String, PropertyInformation> propertyMap =
        convertToMapOfPropertyNamesToPropertyInformation(javaBeanInformation);
    PropertyInformation property = propertyMap.get("age");
    assertThat("Incorrect property read method.", property.getReadMethod().getName(), is("getAge"));
    assertThat("Incorrect property write method.", property.getWriteMethod(), is(nullValue()));
    assertThat("Incorrect property readability.", property.isReadable(), is(true));
    assertThat("Incorrect property writability.", property.isWritable(), is(false));
    assertThat("Incorrect property writability.", property.isReadableWritable(), is(false));
  }


  @Test
  public void shouldHaveDateOfBirthPropertyInformationOfClassPassedToConstructor() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithProperties.class);
    Map<String, PropertyInformation> propertyMap =
        convertToMapOfPropertyNamesToPropertyInformation(javaBeanInformation);
    PropertyInformation property = propertyMap.get("dateOfBirth");
    assertThat("Incorrect property read method.", property.getReadMethod(), is(nullValue()));
    assertThat("Incorrect property write method.", property.getWriteMethod().getName(), is("setDateOfBirth"));
    assertThat("Incorrect property readability.", property.isReadable(), is(false));
    assertThat("Incorrect property writability.", property.isWritable(), is(true));
    assertThat("Incorrect property writability.", property.isReadableWritable(), is(false));
  }


  @Test
  public void shouldHaveFirstNamePropertyInformationOfClassPassedToConstructor() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithProperties.class);
    Map<String, PropertyInformation> propertyMap =
        convertToMapOfPropertyNamesToPropertyInformation(javaBeanInformation);
    PropertyInformation property = propertyMap.get("firstName");
    assertThat("Incorrect property read method.", property.getReadMethod().getName(), is("getFirstName"));
    assertThat("Incorrect property write method.", property.getWriteMethod().getName(), is("setFirstName"));
    assertThat("Incorrect property readability.", property.isReadable(), is(true));
    assertThat("Incorrect property writability.", property.isWritable(), is(true));
    assertThat("Incorrect property writability.", property.isReadableWritable(), is(true));
  }


  @Test
  public void shouldHaveIdPropertyInformationOfClassPassedToConstructor() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithProperties.class);
    Map<String, PropertyInformation> propertyMap =
        convertToMapOfPropertyNamesToPropertyInformation(javaBeanInformation);
    PropertyInformation property = propertyMap.get("id");
    assertThat("Incorrect property read method.", property.getReadMethod().getName(), is("getId"));
    assertThat("Incorrect property write method.", property.getWriteMethod(), is(nullValue()));
    assertThat("Incorrect property readability.", property.isReadable(), is(true));
    assertThat("Incorrect property writability.", property.isWritable(), is(false));
    assertThat("Incorrect property writability.", property.isReadableWritable(), is(false));
  }


  @Test
  public void shouldHaveLastNamePropertyInformationOfClassPassedToConstructor() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithProperties.class);
    Map<String, PropertyInformation> propertyMap =
        convertToMapOfPropertyNamesToPropertyInformation(javaBeanInformation);
    PropertyInformation property = propertyMap.get("lastName");
    assertThat("Incorrect property read method.", property.getReadMethod().getName(), is("getLastName"));
    assertThat("Incorrect property write method.", property.getWriteMethod().getName(), is("setLastName"));
    assertThat("Incorrect property readability.", property.isReadable(), is(true));
    assertThat("Incorrect property writability.", property.isWritable(), is(true));
    assertThat("Incorrect property writability.", property.isReadableWritable(), is(true));
  }


  @Test
  public void shouldHaveNoPropertyInformationWhenConstructorPassedBeanWithNoProperties() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithNoProperties.class);
    assertThat("Incorrect properties.", javaBeanInformation.getProperties().isEmpty(), is(true));
  }


  @Test
  public void shouldHaveNoPropertyNamesWhenConstructorPassedBeanWithNoProperties() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithNoProperties.class);
    assertThat("Incorrect propertyNames.", javaBeanInformation.getPropertyNames().isEmpty(), is(true));
  }


  @Test
  public void shouldHavePropertyNamesOfClassPassedToConstructor() throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithProperties.class);
    assertThat("Incorrect number of propertyNames.", javaBeanInformation.getPropertyNames().size(),
        is(EXPECTED_PROPERTIES.size())
    );
    assertThat("Incorrect propertyNames.", EXPECTED_PROPERTIES.containsAll(javaBeanInformation.getPropertyNames()),
        is(true)
    );
  }


  @Test
  public void shouldHaveSameBeanClassAsClassPassedToConstructor() throws Exception {
    Class<BeanWithProperties> beanClass = BeanWithProperties.class;
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(beanClass);
    assertEquals("Incorrect beanClass.", javaBeanInformation.getBeanClass(), beanClass);
  }


  @Test
  public void shouldHaveSameNumberOfPropertyInformationObjectsAsNumberOfPropertiesInClassPassedToConstructor()
      throws Exception {
    JavaBeanInformation javaBeanInformation = new JavaBeanInformation(BeanWithProperties.class);
    assertThat("Incorrect number of properties.", javaBeanInformation.getProperties().size(),
        is(EXPECTED_PROPERTIES.size())
    );
  }


  @Test(expected = IllegalArgumentException.class)
  public void shouldPreventIllegalBeanClass() throws Exception {
    new JavaBeanInformation(null);
  }

}
