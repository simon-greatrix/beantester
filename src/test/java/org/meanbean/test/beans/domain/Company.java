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

package org.meanbean.test.beans.domain;

import java.util.Objects;

public class Company {

  private Address address;

  private String companyNumber;

  private Long id;

  private String name;

  private String vatNumber;


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Company)) {
      return false;
    }
    Company other = (Company) obj;
    return Objects.equals(address, other.address)
        && Objects.equals(companyNumber, other.companyNumber)
        && Objects.equals(name, other.name)
        && Objects.equals(vatNumber, other.vatNumber);
  }


  public Address getAddress() {
    return address;
  }


  public String getCompanyNumber() {
    return companyNumber;
  }


  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public String getVatNumber() {
    return vatNumber;
  }


  @Override
  public int hashCode() {
    return Objects.hash(address, companyNumber, name, vatNumber);
  }


  public void setAddress(Address address) {
    this.address = address;
  }


  public void setCompanyNumber(String companyNumber) {
    this.companyNumber = companyNumber;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setVatNumber(String vatNumber) {
    this.vatNumber = vatNumber;
  }


  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + name + "]";
  }

}
