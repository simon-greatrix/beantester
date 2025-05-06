package com.pippsford.beantester.factories.json;

import static com.pippsford.beantester.ValueType.RANDOM;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueFactory;
import com.pippsford.beantester.factories.basic.FactoryHolder;
import com.pippsford.beantester.factories.basic.SizeHolder;
import com.pippsford.beantester.factories.basic.StringValueFactory;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

public class JsonObjectFactory extends ValueFactory {

  static JsonObject random(SizeHolder size, FactoryHolder holder) {
    int length = TestContext.get().getRandom().nextInt(size.getSize());
    JsonObjectBuilder builder = Json.createObjectBuilder();
    for (int i = 0; i < length; i++) {
      builder.add(
          (String) StringValueFactory.ALPHANUMERIC.create(RANDOM),
          (JsonValue) holder.getValueFactory().create(RANDOM)
      );
    }
    return builder.build();
  }


  private final FactoryHolder holder;

  private final SizeHolder size;


  public JsonObjectFactory(ValueFactory factory) {
    this(new SizeHolder(), new FactoryHolder(factory));
  }


  private JsonObjectFactory(SizeHolder size, FactoryHolder holder) {
    super(
        JsonObject.class,
        false,
        () -> Json.createObjectBuilder().add("x", 1).build(),
        () -> Json.createObjectBuilder().add("y", 2).build(),
        () -> random(size, holder)
    );
    this.size = size;
    this.holder = holder;
  }


  public int getMaxSize() {
    return size.getSize();
  }


  public ValueFactory getValueFactory() {
    return holder.getValueFactory();
  }


  public JsonObjectFactory setMaxSize(int newSize) {
    size.setSize(newSize);
    return this;
  }


  public JsonObjectFactory setValueFactory(ValueFactory valueFactory) {
    holder.setValueFactory(valueFactory);
    return this;
  }

}
