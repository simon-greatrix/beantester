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

import java.util.List;

public class Product {

  private String description;

  private Long id;

  private Money price;

  private List<Review> reviews;

  private String title;


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
    Product other = (Product) obj;
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (price == null) {
      if (other.price != null) {
        return false;
      }
    } else if (!price.equals(other.price)) {
      return false;
    }
    if (reviews == null) {
      if (other.reviews != null) {
        return false;
      }
    } else if (!reviews.equals(other.reviews)) {
      return false;
    }
    if (title == null) {
      if (other.title != null) {
        return false;
      }
    } else if (!title.equals(other.title)) {
      return false;
    }
    return true;
  }


  public String getDescription() {
    return description;
  }


  public Long getId() {
    return id;
  }


  public Money getPrice() {
    return price;
  }


  public List<Review> getReviews() {
    return reviews;
  }


  public String getTitle() {
    return title;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((price == null) ? 0 : price.hashCode());
    result = prime * result + ((reviews == null) ? 0 : reviews.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setPrice(Money price) {
    this.price = price;
  }


  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }


  public void setTitle(String title) {
    this.title = title;
  }

}
