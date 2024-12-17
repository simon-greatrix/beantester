package io.setl.beantester;

import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.setl.beantester.example.ApproverAccount;
import io.setl.beantester.example.ApproverLink;
import io.setl.beantester.example.ApproverManifest;
import io.setl.beantester.example.ApproverTransfer;
import io.setl.beantester.example.BalanceTypeIdentifier;
import io.setl.beantester.example.BuildableBean;
import io.setl.beantester.example.LedgerAccount;
import io.setl.beantester.example.LedgerManifest;
import io.setl.beantester.example.LedgerTransfer;
import io.setl.beantester.example.PetRecord;
import io.setl.beantester.info.Specs;
import io.setl.beantester.mirror.SerializableLambdas.SerializableFunction2;
import io.setl.beantester.mirror.SerializableLambdas.SerializableLambda;

public class TestBeans {


  @Test
  void testBalanceTypeIdentifier() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, BalanceTypeIdentifier.class);
  }

  @Test
  void testApproverManifest() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, ApproverManifest.class, Specs.notNull("tags"));
  }


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
  void testApproverTransfer() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, ApproverTransfer.class, Specs.notNull("tags"));
  }


  @Test
  void testBuildableBean() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, BuildableBean.class, Specs.notNull("tags"));
  }


  @Test
  void testPetRecord() throws Throwable {
    TestContext testContext = new TestContext();
    BeanVerifier.verify(testContext, PetRecord.class);
  }


  @Test
  void testLedgerAccount() throws Throwable {
    TestContext testContext = new TestContext();
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

}
