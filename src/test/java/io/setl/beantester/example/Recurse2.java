package io.setl.beantester.example;


import jakarta.annotation.Nonnull;
import lombok.Data;

@Data
public class Recurse2 {
  @Nonnull
  private final String aString;

  private Recurse3 recurse3;

  private Recurse1 recurse1;

  void toString(int i, StringBuilder builder) {
    builder.append("{\n")
        .append("  ".repeat(i)).append("aString: ").append(aString).append("\n")
        .append("  ".repeat(i)).append("recurse1: ");
    if( recurse1!=null ) { recurse1.toString(i+1,builder); }
    else { builder.append("null"); }

    builder.append("\n").append("  ".repeat(i)).append("recurse3 ");
    if( recurse3!=null ) { recurse3.toString(i+1,builder); }
    else { builder.append("null"); }

    builder.append("\n").append("  ".repeat(i - 1)).append("}");
  }

}
