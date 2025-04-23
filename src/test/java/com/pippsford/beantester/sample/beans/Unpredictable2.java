package com.pippsford.beantester.sample.beans;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Unpredictable2 {

  public static AtomicInteger counter = new AtomicInteger(0);

  private Integer id;

  private Integer thing;


  public Unpredictable2() {
    id = counter.incrementAndGet();
    if (id % 3 == 0) {
      id = null;
    }
    thing = counter.incrementAndGet();
  }


  public Integer getId() {
    return id;
  }


  public void setId(Integer id) {
    this.id = id;
  }


  public void setThing(Integer thing) {
    this.thing = thing;
  }


  public Integer getThing() {
    return thing;
  }

}
