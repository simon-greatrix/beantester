package com.pippsford.beantester.factories.basic;

import com.pippsford.beantester.ValueFactory;
import lombok.Getter;
import lombok.Setter;

public class FactoryHolder {

  public FactoryHolder(ValueFactory valueFactory) {
    this.valueFactory = valueFactory;
  }


  @Getter
  @Setter
  private ValueFactory valueFactory;

}
