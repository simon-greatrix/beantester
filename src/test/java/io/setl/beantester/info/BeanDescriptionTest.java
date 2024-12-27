package io.setl.beantester.info;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.ApproverManifest;

class BeanDescriptionTest {

  @Test
  void testConstructor() {
    BeanDescription info = BeanDescription.create(ApproverManifest.class);
    System.out.println(info.toString());
    assertNotNull(info);
  }

}
