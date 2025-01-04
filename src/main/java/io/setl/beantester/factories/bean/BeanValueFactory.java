package io.setl.beantester.factories.bean;

import lombok.Getter;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.ValueType;
import io.setl.beantester.info.BeanDescription;

/** A value factory specifically for beans. */
public class BeanValueFactory extends ValueFactory {

  @Getter
  private final BeanDescription description;


  /**
   * New instance.
   *
   * @param suppliers the suppliers instance that will be used to create the values
   */
  BeanValueFactory(Suppliers suppliers) {
    super(
        suppliers.description.getBeanClass(),
        false,
        () -> suppliers.create(ValueType.PRIMARY),
        () -> suppliers.create(ValueType.SECONDARY),
        () -> suppliers.create(ValueType.RANDOM)
    );
    description = suppliers.description;
  }

}
