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

import java.util.Date;
import java.util.List;


public class Order {

  private Address billingAddress;

  private Date completionDate;

  private Date creationDate;

  private Employee handler;

  private Long id;

  private List<Item> items;

  private Address shippingAddress;

  private ShippingCompany shippingCompany;

  private Status status;


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Order other = (Order) obj;
    if (billingAddress == null) {
      if (other.billingAddress != null) {
        return false;
      }
    } else if (!billingAddress.equals(other.billingAddress)) {
      return false;
    }
    if (completionDate == null) {
      if (other.completionDate != null) {
        return false;
      }
    } else if (!completionDate.equals(other.completionDate)) {
      return false;
    }
    if (creationDate == null) {
      if (other.creationDate != null) {
        return false;
      }
    } else if (!creationDate.equals(other.creationDate)) {
      return false;
    }
    if (handler == null) {
      if (other.handler != null) {
        return false;
      }
    } else if (!handler.equals(other.handler)) {
      return false;
    }
    if (items == null) {
      if (other.items != null) {
        return false;
      }
    } else if (!items.equals(other.items)) {
      return false;
    }
    if (shippingAddress == null) {
      if (other.shippingAddress != null) {
        return false;
      }
    } else if (!shippingAddress.equals(other.shippingAddress)) {
      return false;
    }
    if (shippingCompany == null) {
      if (other.shippingCompany != null) {
        return false;
      }
    } else if (!shippingCompany.equals(other.shippingCompany)) {
      return false;
    }
    if (status != other.status) {
      return false;
    }
    return true;
  }


  public Address getBillingAddress() {
    return billingAddress;
  }


  public Date getCompletionDate() {
    return completionDate;
  }


  public Date getCreationDate() {
    return creationDate;
  }


  public Employee getHandler() {
    return handler;
  }


  public Long getId() {
    return id;
  }


  public List<Item> getItems() {
    return items;
  }


  public Address getShippingAddress() {
    return shippingAddress;
  }


  public ShippingCompany getShippingCompany() {
    return shippingCompany;
  }


  public Status getStatus() {
    return status;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((billingAddress == null) ? 0 : billingAddress.hashCode());
    result = prime * result + ((completionDate == null) ? 0 : completionDate.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((handler == null) ? 0 : handler.hashCode());
    result = prime * result + ((items == null) ? 0 : items.hashCode());
    result = prime * result + ((shippingAddress == null) ? 0 : shippingAddress.hashCode());
    result = prime * result + ((shippingCompany == null) ? 0 : shippingCompany.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    return result;
  }


  public void setBillingAddress(Address billingAddress) {
    this.billingAddress = billingAddress;
  }


  public void setCompletionDate(Date completionDate) {
    this.completionDate = completionDate;
  }


  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }


  public void setHandler(Employee handler) {
    this.handler = handler;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setItems(List<Item> items) {
    this.items = items;
  }


  public void setShippingAddress(Address shippingAddress) {
    this.shippingAddress = shippingAddress;
  }


  public void setShippingCompany(ShippingCompany shippingCompany) {
    this.shippingCompany = shippingCompany;
  }


  public void setStatus(Status status) {
    this.status = status;
  }

}
