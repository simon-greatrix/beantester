package org.meanbean.bean.info;

import org.junit.Test;
import org.meanbean.example.BuildableBean;
import org.meanbean.example.BuildableBean.BuildableBeanBuilder;

public class BuildableBeanInformationTest {

  @Test
  public void test() throws Throwable {
    BuildableBeanInformation buildableBeanInformation = new BuildableBeanInformation(BuildableBean.class);
    BuildableBeanBuilder builder = (BuildableBeanBuilder) buildableBeanInformation.newBuilder();
    BuildableBean buildableBean = (BuildableBean) buildableBeanInformation.newBean(builder);
    System.out.println(buildableBean);
  }

}
