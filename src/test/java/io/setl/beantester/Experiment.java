package io.setl.beantester;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.ApproverAccount;
import io.setl.beantester.example.ApproverLink;
import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.example.ApproverTransfer;
import io.setl.beantester.example.BuildableBean;
import io.setl.beantester.example.PetRecord;
import io.setl.beantester.info.Specs;

public class Experiment {

  @Test
  void test() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, ApproverManifest.class, Specs.notNull("tags"));
    BeanVerifier.verify(testContext, ApproverAccount.class);
    BeanVerifier.verify(testContext, ApproverLink.class, Specs.notNull("tags"));
    BeanVerifier.verify(testContext, ApproverTransfer.class, Specs.notNull("tags"));
    BeanVerifier.verify(testContext, BuildableBean.class, Specs.notNull("tags"));
    BeanVerifier.verify(testContext, PetRecord.class);
  }

}
