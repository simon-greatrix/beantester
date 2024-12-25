package io.setl.beantester.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.setl.beantester.TestContext;
import io.setl.beantester.example.BuildableBean;

class BeanDescriptionTest {

  @Test
  void testConstructor() {
    BeanDescription info = BeanDescription.create(BuildableBean.class);
    System.out.println(info.toString());
    assertNotNull(info);
  }

}
