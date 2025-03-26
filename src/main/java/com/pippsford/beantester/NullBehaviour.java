package com.pippsford.beantester;

/**
 * Possible behaviour when a property is set to null.
 *
 * <p>A property could have more than one behaviour. For example, a primary key could be null until the record is inserted into a database.</p>
 */
public enum NullBehaviour {
  /** The property is set to null. This is the assumed behaviour for any property that <b>does not</b> declare a not-null or non-null annotation. */
  NULL,

  /** The property is set to a standard non-null value. */
  VALUE,

  /** An exception is thrown. This is the assumed behaviour for any property that <b>does</b> declare a not-null or non-null annotation. */
  ERROR,

  /** No exception is thrown, but the property cannot be read. */
  NOT_READABLE
}
