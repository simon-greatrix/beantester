package io.setl.beantester;

import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.info.Specs;
import io.setl.beantester.test.Equals;
import io.setl.beantester.test.ReadWrite;

/**
 * Verify beans.
 */
public class BeanVerifier {

  /**
   * Verify the bean with the given specs.
   *
   * @param clazz the class to verify
   * @param specs the specifications to customise the verification
   */
  public static void verify(Class<?> clazz, Specs.Spec... specs) {
    TestContext testContext = new TestContext();
    verify(testContext, clazz, specs);
  }


  /**
   * Verify the bean with the given specs.
   *
   * @param testContext the test context
   * @param clazz       the class to verify
   * @param specs       the specifications to customise the verification
   */
  public static void verify(TestContext testContext, Class<?> clazz, Specs.Spec... specs) {
    BeanDescription info = BeanDescription.create(testContext, clazz, specs);

    BeanHolder h = info.createHolder();

    ReadWrite readWrite = new ReadWrite(h);
    readWrite.test();

    Equals equals = new Equals(h);
    equals.test();
  }

}
