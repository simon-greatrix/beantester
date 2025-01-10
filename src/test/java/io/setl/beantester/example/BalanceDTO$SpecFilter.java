package io.setl.beantester.example;

import java.math.BigDecimal;
import java.util.List;

import io.setl.beantester.info.Specs;
import io.setl.beantester.info.Specs.BeanCreatorSpec;
import io.setl.beantester.info.Specs.Spec;
import io.setl.beantester.info.specs.SpecFilter;

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
