package com.pippsford.beantester;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pippsford.beantester.sample.protobuf.AllTypes;
import com.pippsford.beantester.sample.protobuf.MessageWithOneOf;
import com.pippsford.beantester.sample.protobuf.NestedMessage;
import org.junit.jupiter.api.Test;

import com.pippsford.beantester.factories.FactoryRepository;
import com.pippsford.beantester.factories.basic.StringValueFactory;
public class TestProtobuf {

  @Test
  public void test() {
    TestContext.get().setRepeatable("protobuf".hashCode());
    FactoryRepository repository = TestContext.get().getFactories();
    repository.addFactory(StringValueFactory.ALPHANUMERIC);
    AllTypes complexMessage = TestContext.get().create(AllTypes.class, ValueType.RANDOM);
    assertEquals(
        "double_value: -0.6370892033689571\n"
            + "float_value: 1.5371308\n"
            + "int32_value: 1779696989\n"
            + "int64_value: -237056793196910493\n"
            + "uint32_value: 3364659241\n"
            + "uint64_value: 16412056902061895884\n"
            + "sint32_value: -552194644\n"
            + "sint64_value: -8834602417107922811\n"
            + "fixed32_value: 1073898316\n"
            + "fixed64_value: 5704508049764654863\n"
            + "sfixed32_value: 1999847990\n"
            + "sfixed64_value: 2001675825751407521\n"
            + "string_value: \"4SWgs\"\n"
            + "bytes_value: \"\\322\\232H\\224\"\n"
            + "day_of_the_week: SUNDAY\n"
            + "message_with_one_of {\n"
            + "  label: \"hxWFj\"\n"
            + "}\n"
            + "map_value {\n"
            + "  key: \"LKB3G1z1PV\"\n"
            + "  value: 904437164\n"
            + "}\n"
            + "map_value {\n"
            + "  key: \"evXxWgU\"\n"
            + "  value: -961017225\n"
            + "}\n"
            + "map_value2 {\n"
            + "  key: -1888855369\n"
            + "  value: \"1U76ldBk\"\n"
            + "}\n"
            + "repeated_bool_value: true\n"
            + "repeated_bool_value: true\n"
            + "repeated_bool_value: true\n"
            + "repeated_int_value: -822315803\n"
            + "repeated_int_value: -1401513338\n",
        complexMessage.toString()
    );


    complexMessage = TestContext.get().create(AllTypes.class, ValueType.PRIMARY);
    assertEquals(
        "double_value: 1.0\n"
            + "float_value: 1.0\n"
            + "int32_value: 1\n"
            + "int64_value: 1\n"
            + "uint32_value: 1\n"
            + "uint64_value: 1\n"
            + "sint32_value: 1\n"
            + "sint64_value: 1\n"
            + "fixed32_value: 1\n"
            + "fixed64_value: 1\n"
            + "sfixed32_value: 1\n"
            + "sfixed64_value: 1\n"
            + "bool_value: true\n"
            + "string_value: \"x\"\n"
            + "bytes_value: \"X\"\n"
            + "message_with_one_of {\n"
            + "  label: \"x\"\n"
            + "}\n"
            + "map_value {\n"
            + "  key: \"x\"\n"
            + "  value: 1\n"
            + "}\n"
            + "map_value2 {\n"
            + "  key: 1\n"
            + "  value: \"x\"\n"
            + "}\n"
            + "repeated_bool_value: true\n"
            + "repeated_int_value: 1\n",
        complexMessage.toString()
    );


    complexMessage = TestContext.get().create(AllTypes.class, ValueType.SECONDARY);
    assertEquals(
        "double_value: 1.25\n"
            + "float_value: 1.25\n"
            + "int32_value: 2\n"
            + "int64_value: 2\n"
            + "uint32_value: 2\n"
            + "uint64_value: 2\n"
            + "sint32_value: 2\n"
            + "sint64_value: 2\n"
            + "fixed32_value: 2\n"
            + "fixed64_value: 2\n"
            + "sfixed32_value: 2\n"
            + "sfixed64_value: 2\n"
            + "string_value: \"y\"\n"
            + "bytes_value: \"Y\"\n"
            + "day_of_the_week: MONDAY\n"
            + "message_with_one_of {\n"
            + "  name: \"y\"\n"
            + "}\n"
            + "map_value {\n"
            + "  key: \"y\"\n"
            + "  value: 2\n"
            + "}\n"
            + "map_value {\n"
            + "  key: \"y\"\n"
            + "  value: 2\n"
            + "}\n"
            + "map_value2 {\n"
            + "  key: 2\n"
            + "  value: \"y\"\n"
            + "}\n"
            + "map_value2 {\n"
            + "  key: 2\n"
            + "  value: \"y\"\n"
            + "}\n"
            + "repeated_bool_value: false\n"
            + "repeated_bool_value: false\n"
            + "repeated_int_value: 2\n"
            + "repeated_int_value: 2\n",
        complexMessage.toString()
    );

  }



  @Test
  public void testNested() {
    TestContext.get().setRepeatable("protobuf".hashCode());
    FactoryRepository repository = TestContext.get().getFactories();
    repository.addFactory(StringValueFactory.ALPHANUMERIC);
    NestedMessage message = TestContext.get().create(NestedMessage.class, ValueType.RANDOM);
    assertEquals(
        "value: -1342186371\n"
            + "nested {\n"
            + "  value: 1779696989\n"
            + "  nested {\n"
            + "  }\n"
            + "}\n",
        message.toString()
    );


    message = TestContext.get().create(NestedMessage.class, ValueType.PRIMARY);
    assertEquals(
        "value: 1\n"
            + "nested {\n"
            + "  value: 1\n"
            + "  nested {\n"
            + "  }\n"
            + "}\n",
        message.toString()
    );


    message = TestContext.get().create(NestedMessage.class, ValueType.SECONDARY);
    assertEquals(
        "value: 2\n"
            + "nested {\n"
            + "  value: 2\n"
            + "  nested {\n"
            + "  }\n"
            + "}\n",
        message.toString()
    );


    TestContext.get().setStructureDepth(5);
    message = TestContext.get().create(NestedMessage.class, ValueType.PRIMARY);
    assertEquals(
        "value: 1\n"
            + "nested {\n"
            + "  value: 1\n"
            + "  nested {\n"
            + "    value: 1\n"
            + "    nested {\n"
            + "      value: 1\n"
            + "      nested {\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}\n",
        message.toString()
    );

  }


  @Test
  void testBuilders() {
    TestContext.get().setRepeatable("protobuf".hashCode());
    FactoryRepository repository = TestContext.get().getFactories();
    repository.addFactory(StringValueFactory.ALPHANUMERIC);
    MessageWithOneOf.Builder builder = TestContext.get().create(MessageWithOneOf.Builder.class, ValueType.PRIMARY);
    assertEquals(
        "label: \"x\"\n",
        builder.build().toString()
    );

    builder = TestContext.get().create(MessageWithOneOf.Builder.class, ValueType.SECONDARY);
    assertEquals(
        "name: \"y\"\n",
        builder.build().toString()
    );
  }

}
