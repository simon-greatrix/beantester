package io.setl.beantester;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.ApproverAccount;
import io.setl.beantester.example.ApproverLink;
import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.example.ApproverTransfer;
import io.setl.beantester.example.BalanceDTO;
import io.setl.beantester.example.BalanceTypeIdentifier;
import io.setl.beantester.example.BuildableBean;
import io.setl.beantester.example.LedgerAccount;
import io.setl.beantester.example.LedgerManifest;
import io.setl.beantester.example.LedgerTransfer;
import io.setl.beantester.example.PetRecord;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.Specs;

public class TestBeans {


  @Test
  void testApproverAccount() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, ApproverAccount.class);
  }


  @Test
  void testApproverLink() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, ApproverLink.class, Specs.notNull("tags"));
  }


  @Test
  void testApproverManifest() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, ApproverManifest.class, Specs.notNull("tags"));
  }


  @Test
  void testApproverTransfer() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, ApproverTransfer.class, Specs.notNull("tags"));
  }


  @Test
  void testBalanceTypeIdentifier() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, BalanceTypeIdentifier.class);
  }


  @Test
  void testBuildableBean() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, BuildableBean.class, Specs.notNull("tags"));
  }


  @Test
  void testLedgerAccount() throws Throwable {
    TestContext testContext = new TestContext();
    testContext.getFactories()
        .addFactory(BeanDescription.create(testContext, BalanceDTO.class,
            Specs.beanMaker("of", String.class, BigDecimal.class)
        ));
    BeanVerifier.verify(testContext, LedgerAccount.class);
  }


  @Test
  void testLedgerManifest() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, LedgerManifest.class);
  }


  @Test
  void testLedgerTransfer() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, LedgerTransfer.class);
  }


  @Test
  void testPetRecord() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, PetRecord.class);
  }

}
