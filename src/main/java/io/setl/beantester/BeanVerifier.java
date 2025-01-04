package io.setl.beantester;

import java.util.Optional;

import io.setl.beantester.factories.bean.BeanValueFactory;
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
    TestContext.close();
    verifyWithContext(clazz, specs);
    TestContext.close();
  }


  /**
   * Verify the bean with the given specs.
   *
   * @param clazz the class to verify
   * @param specs the specifications to customise the verification
   */
  public static void verifyWithContext(Class<?> clazz, Specs.Spec... specs) {
    BeanDescription info;
    try {
      info = BeanDescription.create(clazz, specs);
    } catch (RuntimeException e) {
      // Can't create automatically, but we may have a registered factory.
      Optional<ValueFactory> factory = TestContext.get().getFactories().tryGetFactory(clazz);
      if (factory.isPresent() && specs.length == 0 && factory.get() instanceof BeanValueFactory bvf) {
        info = bvf.getDescription();
      } else {
        throw e;
      }
    }

    info.getBeanCreator().validate(info);

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
