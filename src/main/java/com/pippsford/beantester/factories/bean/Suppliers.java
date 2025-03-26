package com.pippsford.beantester.factories.bean;

import java.util.LinkedList;

import com.pippsford.beantester.ValueType;
import com.pippsford.beantester.info.BeanDescription;
import com.pippsford.beantester.info.BeanHolder;

/**
 * Supplies beans. If a bean type has a recursive reference to itself the two beans must be created independently. This class manages the multiple BeanHolders
 * required for that.
 */
class Suppliers {

  final BeanDescription description;

  private final LinkedList<BeanHolder> holders = new LinkedList<>();


  Suppliers(BeanDescription description) {
    this.description = description;
  }


  Object create(ValueType type) {
    BeanHolder holder;
    if (holders.isEmpty()) {
      holder = description.createHolder();
    } else {
      holder = holders.removeLast();
    }
    try {
      return holder.create(type);
    } finally {
      holders.add(holder);
    }
  }

}
