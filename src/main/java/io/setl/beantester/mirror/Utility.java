package io.setl.beantester.mirror;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions that do not really belong to any class in particular.
 */
// @since 6.0 methods are no longer final
abstract class Utility {

  /* How many chars have been consumed
   * during parsing in typeSignatureToString().
   * Read by methodSignatureToString().
   * Set by side effect, but only internally.
   */
  private static final ThreadLocal<Integer> consumed_chars = new ThreadLocal<Integer>() {
    @Override
    protected Integer initialValue() {
      return Integer.valueOf(0);
    }
  };


  /**
   * Shorten long class names, <em>java/lang/String</em> becomes
   * <em>java.lang.String</em>,
   * e.g. If <em>chopit</em> is <em>true</em> the prefix <em>java.lang</em>
   * is also removed.
   *
   * @param str    The long class name
   *
   * @return Compacted class name
   */
  public static String compactClassName(final String str) {
    return str.replace('/', '.');
  }


  /**
   * Converts argument list portion of method signature to string.
   *
   * @param signature Method signature
   *
   * @return String Array of argument types
   */
  public static String[] methodSignatureArgumentTypes(final String signature)
      throws IllegalArgumentException {
    final List<String> vec = new ArrayList<>();
    int index;
    try {
      // Skip any type arguments to read argument declarations between '(' and ')'
      index = signature.indexOf('(') + 1;
      if (index <= 0) {
        throw new IllegalArgumentException("Invalid method signature: " + signature);
      }
      while (signature.charAt(index) != ')') {
        vec.add(typeSignatureToString(signature.substring(index)));
        //corrected concurrent private static field access
        index += unwrap(consumed_chars); // update position
      }
    } catch (final StringIndexOutOfBoundsException e) { // Should never occur
      throw new IllegalArgumentException("Invalid method signature: " + signature, e);
    }
    return vec.toArray(String[]::new);
  }


  /**
   * This method converts a type signature string into a Java type declaration such as
   * 'String[]' and throws a 'ClassFormatException' when the parsed type is invalid.
   *
   * @param signature type signature
  *
   * @return string containing human-readable type signature
   *
   * @since 6.4.0
   */
  public static String typeSignatureToString(final String signature) throws IllegalArgumentException {
    //corrected concurrent private static field access
    wrap(consumed_chars, 1); // This is the default, read just one char like `B'
    try {
      switch (signature.charAt(0)) {
        case 'B':
          return "byte";
        case 'C':
          return "char";
        case 'D':
          return "double";
        case 'F':
          return "float";
        case 'I':
          return "int";
        case 'J':
          return "long";
        case 'T': { // TypeVariableSignature
          final int index = signature.indexOf(';'); // Look for closing ';'
          if (index < 0) {
            throw new IllegalArgumentException("Invalid type variable signature: " + signature);
          }
          //corrected concurrent private static field acess
          wrap(consumed_chars, index + 1); // "Tblabla;" `T' and `;' are removed
          return compactClassName(signature.substring(1, index));
        }
        case 'L': { // Full class name
          // should this be a while loop? can there be more than
          // one generic clause?  (markro)
          int fromIndex = signature.indexOf('<'); // generic type?
          if (fromIndex < 0) {
            fromIndex = 0;
          } else {
            fromIndex = signature.indexOf('>', fromIndex);
            if (fromIndex < 0) {
              throw new IllegalArgumentException("Invalid signature: " + signature);
            }
          }
          final int index = signature.indexOf(';', fromIndex); // Look for closing `;'
          if (index < 0) {
            throw new IllegalArgumentException("Invalid signature: " + signature);
          }

          // check to see if there are any TypeArguments
          final int bracketIndex = signature.substring(0, index).indexOf('<');
          if (bracketIndex < 0) {
            // just a class identifier
            wrap(consumed_chars, index + 1); // "Lblabla;" `L' and `;' are removed
            return compactClassName(signature.substring(1, index));
          }
          // but make sure we are not looking past the end of the current item
          fromIndex = signature.indexOf(';');
          if (fromIndex < 0) {
            throw new IllegalArgumentException("Invalid signature: " + signature);
          }
          if (fromIndex < bracketIndex) {
            // just a class identifier
            wrap(consumed_chars, fromIndex + 1); // "Lblabla;" `L' and `;' are removed
            return compactClassName(signature.substring(1, fromIndex));
          }

          // we have TypeArguments; build up partial result
          // as we recurse for each TypeArgument
          final StringBuilder type = new StringBuilder(compactClassName(signature.substring(1, bracketIndex)))
              .append("<");
          int consumed_chars = bracketIndex + 1; // Shadows global var

          // check for wildcards
          if (signature.charAt(consumed_chars) == '+') {
            type.append("? extends ");
            consumed_chars++;
          } else if (signature.charAt(consumed_chars) == '-') {
            type.append("? super ");
            consumed_chars++;
          }

          // get the first TypeArgument
          if (signature.charAt(consumed_chars) == '*') {
            type.append("?");
            consumed_chars++;
          } else {
            type.append(typeSignatureToString(signature.substring(consumed_chars)));
            // update our consumed count by the number of characters the for type argument
            consumed_chars = unwrap(Utility.consumed_chars) + consumed_chars;
            wrap(Utility.consumed_chars, consumed_chars);
          }

          // are there more TypeArguments?
          while (signature.charAt(consumed_chars) != '>') {
            type.append(", ");
            // check for wildcards
            if (signature.charAt(consumed_chars) == '+') {
              type.append("? extends ");
              consumed_chars++;
            } else if (signature.charAt(consumed_chars) == '-') {
              type.append("? super ");
              consumed_chars++;
            }
            if (signature.charAt(consumed_chars) == '*') {
              type.append("?");
              consumed_chars++;
            } else {
              type.append(typeSignatureToString(signature.substring(consumed_chars)));
              // update our consumed count by the number of characters the for type argument
              consumed_chars = unwrap(Utility.consumed_chars) + consumed_chars;
              wrap(Utility.consumed_chars, consumed_chars);
            }
          }

          // process the closing ">"
          consumed_chars++;
          type.append(">");

          if (signature.charAt(consumed_chars) == '.') {
            // we have a ClassTypeSignatureSuffix
            type.append(".");
            // convert SimpleClassTypeSignature to fake ClassTypeSignature
            // and then recurse to parse it
            type.append(typeSignatureToString("L" + signature.substring(consumed_chars + 1)));
            // update our consumed count by the number of characters the for type argument
            // note that this count includes the "L" we added, but that is ok
            // as it accounts for the "." we didn't consume
            consumed_chars = unwrap(Utility.consumed_chars) + consumed_chars;
            wrap(Utility.consumed_chars, consumed_chars);
            return type.toString();
          }
          if (signature.charAt(consumed_chars) != ';') {
            throw new IllegalArgumentException("Invalid signature: " + signature);
          }
          wrap(Utility.consumed_chars, consumed_chars + 1); // remove final ";"
          return type.toString();
        }
        case 'S':
          return "short";
        case 'Z':
          return "boolean";
        case '[': { // Array declaration
          int n;
          StringBuilder brackets;
          String type;
          int consumed_chars; // Shadows global var
          brackets = new StringBuilder(); // Accumulate []'s
          // Count opening brackets and look for optional size argument
          for (n = 0; signature.charAt(n) == '['; n++) {
            brackets.append("[]");
          }
          consumed_chars = n; // Remember value
          // The rest of the string denotes a `<field_type>'
          type = typeSignatureToString(signature.substring(n));
          //corrected concurrent private static field acess
          //Utility.consumed_chars += consumed_chars; is replaced by:
          final int _temp = unwrap(Utility.consumed_chars) + consumed_chars;
          wrap(Utility.consumed_chars, _temp);
          return type + brackets;
        }
        case 'V':
          return "void";
        default:
          throw new IllegalArgumentException("Invalid signature: `" + signature + "'");
      }
    } catch (final StringIndexOutOfBoundsException e) { // Should never occur
      throw new IllegalArgumentException("Invalid signature: " + signature, e);
    }
  }


  private static int unwrap(final ThreadLocal<Integer> tl) {
    return tl.get().intValue();
  }


  private static void wrap(final ThreadLocal<Integer> tl, final int value) {
    tl.set(Integer.valueOf(value));
  }

}
