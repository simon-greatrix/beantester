package com.pippsford.beantester;

/** An enumeration of the type of values that can be generated by a value factory. */
public enum ValueType {
  /** A random value, probably different every time. */
  RANDOM,

  /** A fixed "primary" value. */
  PRIMARY,

  /** A fixed "secondary" value, which is different from the primary. */
  SECONDARY
}
