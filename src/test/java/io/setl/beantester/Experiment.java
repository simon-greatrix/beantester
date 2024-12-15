package io.setl.beantester;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.info.BeanInformation;
import io.setl.beantester.info.Specs;
import io.setl.beantester.test.Equals;
import io.setl.beantester.test.ReadWrite;

public class Experiment {

  @Test
  void test() throws Throwable {
    TestContext testContext = new TestContext();
    BeanInformation info = BeanInformation.create(testContext, ApproverManifest.class,
        Specs.creatorProperties(Specs.notNull("tags")),
        Specs.notNull("tags"));
    BeanHolder h = info.createHolder();

    ReadWrite readWrite = new ReadWrite(h);
    readWrite.test();

    Equals equals = new Equals(h);
    equals.test();
  }

}
