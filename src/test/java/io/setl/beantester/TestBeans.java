package io.setl.beantester;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.setl.beantester.example.ApproverAccount;
import io.setl.beantester.example.ApproverLink;
import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.example.ApproverTransfer;
import io.setl.beantester.example.BalanceDTO;
import io.setl.beantester.example.BalanceTypeIdentifier;
import io.setl.beantester.example.LedgerAccount;
import io.setl.beantester.example.LedgerManifest;
import io.setl.beantester.example.LedgerTransfer;
import io.setl.beantester.example.PetRecord;
import io.setl.beantester.info.BeanDescription;
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
    BeanVerifier.verify(ApproverManifest.class);
  }


  @Test
  void testApproverTransfer() throws Throwable {
    BeanVerifier.verify(ApproverTransfer.class);
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
    BeanVerifier.verify(LedgerAccount.class);
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

}
