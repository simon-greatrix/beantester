package com.pippsford.beantester.factories.util;

import com.pippsford.beantester.ValueFactory;
import lombok.Getter;
import lombok.Setter;

/** Holder for a factory that generates collection members. */
public class FactoryHolder {

  public FactoryHolder(ValueFactory valueFactory) {
    this.valueFactory = valueFactory;
  }


  @Getter
  @Setter
  private ValueFactory valueFactory;

}
