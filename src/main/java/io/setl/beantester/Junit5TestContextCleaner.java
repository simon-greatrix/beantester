package io.setl.beantester;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

/**
 * Automatically close the test context after a test has finished.
 */
public class Junit5TestContextCleaner implements TestExecutionListener {

  @Override
  public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    // If this was a test, close the context
    if (testIdentifier.isTest()) {
      TestContext.close();
    }
  }

}
