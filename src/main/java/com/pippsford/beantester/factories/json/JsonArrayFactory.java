package com.pippsford.beantester.factories.json;

import static com.pippsford.beantester.ValueType.RANDOM;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.util.FactoryHolder;
import com.pippsford.beantester.factories.util.SizeHolder;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonValue;

/** Factory for JSON arrays. */
public class JsonArrayFactory extends ValueFactory {

  static JsonArray random(SizeHolder size, FactoryHolder holder) {
    int length = TestContext.get().getRandom().nextInt(size.getSize());
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (int i = 0; i < length; i++) {
      arrayBuilder.add((JsonValue) holder.getValueFactory().create(RANDOM));
    }
    return arrayBuilder.build();
  }


  private final FactoryHolder factory;

  private final SizeHolder size;


  public JsonArrayFactory(ValueFactory factory) {
    this(new SizeHolder(), new FactoryHolder(factory));
  }


  private JsonArrayFactory(SizeHolder size, FactoryHolder factory) {
    super(
        JsonArray.class,
        false,
        () -> Json.createArrayBuilder().add(1).build(),
        () -> Json.createArrayBuilder().add(2).build(),
        () -> random(size, factory)
    );
    this.size = size;
    this.factory = factory;
  }


  public int getMaxSize() {
    return size.getSize();
  }


  public ValueFactory getValueFactory() {
    return factory.getValueFactory();
  }


  public JsonArrayFactory setMaxSize(int newSize) {
    size.setSize(newSize);
    return this;
  }


  public JsonArrayFactory setValueFactory(ValueFactory valueFactory) {
    factory.setValueFactory(valueFactory);
    return this;
  }

}
