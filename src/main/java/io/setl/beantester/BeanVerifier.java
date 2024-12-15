package io.setl.beantester;

import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.info.BeanInformation;
import io.setl.beantester.info.Specs;
import io.setl.beantester.test.Equals;
import io.setl.beantester.test.ReadWrite;

public class BeanVerifier {

  public static void verify(Class<?> clazz, Specs.Spec... specs) {
    TestContext testContext = new TestContext();
    BeanInformation info = BeanInformation.create(testContext, ApproverManifest.class, specs);

    BeanHolder h = info.createHolder();

    ReadWrite readWrite = new ReadWrite(h);
    readWrite.test();

    Equals equals = new Equals(h);
    equals.test();
  }


  public static void verify(TestContext testContext, Class<?> clazz, Specs.Spec... specs) {
    BeanInformation info = BeanInformation.create(testContext, clazz, specs);

    BeanHolder h = info.createHolder();

    ReadWrite readWrite = new ReadWrite(h);
    readWrite.test();

    Equals equals = new Equals(h);
    equals.test();
  }

}
