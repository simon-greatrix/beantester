package com.pippsford.beantester.info;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.pippsford.beantester.sample.beans.ApproverManifest;

class BeanDescriptionTest {

  @Test
  void testConstructor() {
    BeanDescription info = BeanDescription.create(ApproverManifest.class);
    System.out.println(info.toString());
    assertNotNull(info);
  }

}
