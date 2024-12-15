package io.setl.beantester.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.setl.beantester.TestContext;
import io.setl.beantester.example.BuildableBean;

class BeanInformationTest {

  @Test
  void testConstructor() {
    TestContext testContext = new TestContext();
    BeanInformation info = BeanInformation.create(testContext, BuildableBean.class);
    System.out.println(info.toString());
    assertNotNull(info);
  }

}
