package com.pippsford.beantester.sample.beans;


import lombok.Value;

@Value
public class Recurse3 {

  Recurse1 recurse1;


  void toString(int i, StringBuilder builder) {
    builder.append("{\n")
        .append("  ".repeat(i)).append("recurse1: ");
    if (recurse1 != null) {
      recurse1.toString(i + 1, builder);
    } else {
      builder.append("null");
    }
    builder.append("\n").append("  ".repeat(i - 1)).append("}");
  }


}
