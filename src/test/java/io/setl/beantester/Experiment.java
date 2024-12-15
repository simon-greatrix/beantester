package io.setl.beantester;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.BuildableBean;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.info.BeanInformation;
import io.setl.beantester.test.ReadWrite;

public class Experiment {

  @Test
  void test() throws Throwable {
    TestContext testContext = new TestContext();
    BeanInformation info = BeanInformation.create(testContext, BuildableBean.class);
    BeanHolder h = info.createHolder();

    ReadWrite readWrite = new ReadWrite(h);
    readWrite.test();
  }

}
