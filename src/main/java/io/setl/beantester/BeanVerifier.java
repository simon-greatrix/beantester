package io.setl.beantester;

import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.info.Specs;
import io.setl.beantester.test.Equals;
import io.setl.beantester.test.NullRules;
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
    BeanDescription info = BeanDescription.create(clazz, specs);

    info.beanCreator().validate(info);

    NullRules.inferNullBehaviour(info);
    NullRules.inferOmittedBehaviour(info);
    NullRules.validate(info);

    BeanHolder h = info.createHolder();

    ReadWrite readWrite = new ReadWrite(h);
    readWrite.test();

    Equals equals = new Equals(h);
    equals.test();
  }

}
