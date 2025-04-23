package com.pippsford.beantester;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pippsford.beantester.info.Specs;
import com.pippsford.beantester.sample.beans.Unpredictable;
import org.junit.jupiter.api.Test;

class BeanVerifierTest {

  @Test
  void testUnpredictable() {
    Unpredictable.counter.set(0);

    // 'id' and 'thing' are variable
    assertThrows(AssertionException.class, () -> BeanVerifier.verify(Unpredictable.class));

    // thing is variable, but not specified
    assertThrows(AssertionException.class, () -> BeanVerifier.verify(Unpredictable.class, Specs.onNull(NullBehaviour.VARIABLE_NULLABLE, "id")));

    // id is variable, but not specified
    assertThrows(AssertionException.class, () -> BeanVerifier.verify(Unpredictable.class, Specs.onNull(NullBehaviour.VARIABLE, "thing")));

    assertThrows(
        AssertionException.class, () ->
            BeanVerifier.verifyWithContext(
                Unpredictable.class,
                Specs.onNull(NullBehaviour.VARIABLE, "id"),
                Specs.onNull(NullBehaviour.VARIABLE, "thing")
            )
    );

    BeanVerifier.verify(
        Unpredictable.class,
        Specs.onNull(NullBehaviour.VARIABLE_NULLABLE, "id"),
        Specs.onNull(NullBehaviour.VARIABLE, "thing")
    );
  }

}
