package com.pippsford.beantester.sample.beans;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Builder
@Value
public class Recurse1 {

  static final AtomicLong ID_SRC = new AtomicLong(0);

  int anInt;

  @EqualsAndHashCode.Exclude
  long id = ID_SRC.getAndIncrement();

  Recurse1 recurse1;

  Recurse2 recurse2;


  public String toString() {
    StringBuilder builder = new StringBuilder();
    toString(1, builder);
    return builder.toString();
  }


  void toString(int i, StringBuilder builder) {
    builder.append("{ id=").append(id).append('\n')
        .append("  ".repeat(i)).append("anInt: ").append(anInt).append("\n")
        .append("  ".repeat(i)).append("recurse1: ");
    if (recurse1 != null) {
      recurse1.toString(i + 1, builder);
    } else {
      builder.append("null");
    }

    builder.append("\n").append("  ".repeat(i)).append("recurse2: ");
    if (recurse2 != null) {
      recurse2.toString(i + 1, builder);
    } else {
      builder.append("null");
    }

    builder.append("\n").append("  ".repeat(i - 1)).append("}");
  }

}
