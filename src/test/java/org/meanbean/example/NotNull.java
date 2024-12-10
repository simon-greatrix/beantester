package org.meanbean.example;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/** An example annotation that indicates that a field or parameter cannot be null. */
@Retention(RUNTIME)
@interface NotNull {

}
