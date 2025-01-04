package io.setl.beantester;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.setl.beantester.example.AccountBalanceUpdate;
import io.setl.beantester.example.ApproverAccount;
import io.setl.beantester.example.ApproverLink;
import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.example.ApproverTransfer;
import io.setl.beantester.example.BalanceDTO;
import io.setl.beantester.example.BalanceTypeIdentifier;
import io.setl.beantester.example.LedgerAccount;
import io.setl.beantester.example.LedgerAccount2;
import io.setl.beantester.example.LedgerManifest;
import io.setl.beantester.example.LedgerTransfer;
import io.setl.beantester.example.PetRecord;
import io.setl.beantester.example.Recurse1;
import io.setl.beantester.info.BeanDescription;
import io.setl.beantester.info.BeanHolder;
import io.setl.beantester.info.Specs;

public class TestBeans {


  @AfterEach
  void tearDown() {
    TestContext.close();
  }


  @Test
  void testApproverAccount() throws Throwable {
    BeanVerifier.verify(ApproverAccount.class);
  }


  @Test
  void testApproverLink() throws Throwable {
    BeanVerifier.verify(ApproverLink.class);
  }


  @Test
  void testApproverManifest() throws Throwable {
    TestContext.close();
    TestContext.get().setRepeatable(37L);
    BeanVerifier.verify(ApproverManifest.class);
  }


  @Test
  void testApproverTransfer() throws Throwable {
      TestContext.get().setRepeatable(221);
      BeanVerifier.verify(ApproverTransfer.class, Specs.ignored("links"));

  }


  @Test
  void testBalanceTypeIdentifier() throws Throwable {
    BeanVerifier.verify(BalanceTypeIdentifier.class);
  }


  @Test
  void testLedgerAccount() throws Throwable {
    TestContext testContext = TestContext.get();
    testContext.getFactories()
        .addFactory(BeanDescription.create(
            BalanceDTO.class,
            Specs.beanMaker("of", String.class, BigDecimal.class)
        ));
    BeanVerifier.verifyWithContext(LedgerAccount.class);
  }


  @Test
  void testLedgerAccount2() throws Throwable {
    TestContext testContext = TestContext.get();
    testContext.getFactories()
        .addFactory(BeanDescription.create(
            BalanceDTO.class,
            Specs.beanMaker("of", String.class, BigDecimal.class)
        ));
    BeanVerifier.verifyWithContext(LedgerAccount2.class);
  }


  @Test
  void testLedgerManifest() throws Throwable {
    BeanVerifier.verify(LedgerManifest.class);
  }


  @Test
  void testLedgerTransfer() throws Throwable {
      BeanVerifier.verify(LedgerTransfer.class);
  }


  @Test
  void testPetRecord() throws Throwable {
    BeanVerifier.verify(PetRecord.class);
  }

  @Test
  void testRecurse() {
    BeanVerifier.verify(Recurse1.class);
  }

  @Test
  void testAccountBalanceUpdate() {
    BeanVerifier.verify(AccountBalanceUpdate.class);
  }


  @Test
  void testBalanceDTO1() {
    BeanVerifier.verify(
        BalanceDTO.class,
        Specs.beanMaker("of", String.class, BigDecimal.class)
    );
  }


  @Test
  void testBalanceDTO2() {
    BeanVerifier.verify(
        BalanceDTO.class,
        Specs.beanMaker("of", BalanceTypeIdentifier.class, BigDecimal.class)
    );
  }

}
