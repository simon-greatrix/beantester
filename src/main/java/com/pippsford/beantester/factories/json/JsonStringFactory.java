package com.pippsford.beantester.factories.json;

import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.basic.FactoryHolder;
import com.pippsford.beantester.factories.basic.StringValueFactory;
import jakarta.json.Json;
import jakarta.json.JsonString;

public class JsonStringFactory extends ValueFactory {

  final FactoryHolder factoryHolder;


  private JsonStringFactory(FactoryHolder factory) {
    super(
        JsonString.class,
        vt -> Json.createValue(String.valueOf(factory.getValueFactory().create(vt)))
    );
    factoryHolder = factory;
  }


  public JsonStringFactory() {
    this(new FactoryHolder(new StringValueFactory()));
  }


  public ValueFactory getValueFactory() {
    return factoryHolder.getValueFactory();
  }


  public JsonStringFactory setValueFactory(ValueFactory valueFactory) {
    factoryHolder.setValueFactory(valueFactory);
    return this;
  }

}
