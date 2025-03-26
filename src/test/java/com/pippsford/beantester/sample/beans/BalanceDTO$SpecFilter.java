package com.pippsford.beantester.sample.beans;

import java.math.BigDecimal;
import java.util.List;

import com.pippsford.beantester.info.Specs;
import com.pippsford.beantester.info.Specs.BeanCreatorSpec;
import com.pippsford.beantester.info.Specs.Spec;
import com.pippsford.beantester.info.specs.SpecFilter;

public class BalanceDTO$SpecFilter implements SpecFilter {

  @Override
  public List<Spec> filter(List<Spec> specs) {
    if (SpecFilter.hasSpec(BeanCreatorSpec.class, specs)) {
      return specs;
    }

    specs.add(Specs.beanMaker("of", String.class, BigDecimal.class));

    return specs;
  }

}
