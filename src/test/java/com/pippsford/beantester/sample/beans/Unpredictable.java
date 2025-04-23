package com.pippsford.beantester.sample.beans;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Unpredictable {

  public static AtomicInteger counter = new AtomicInteger(0);

  private Integer id;

  private Integer thing;


  public Integer getId() {
    return id;
  }


  public void setId(Integer id) {
    if (id == null) {
      id = counter.incrementAndGet();
      if (id % 3 == 0) {
        id = null;
      }
    }
    this.id = id;
  }


  public void setThing(Integer thing) {
    if (thing == null) {
      thing = counter.incrementAndGet();
    }
    this.thing = thing;
  }


  public Integer thing() {
    return thing;
  }

}
