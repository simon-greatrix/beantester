package io.setl.beantester;

import org.junit.jupiter.api.Test;

import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.factories.basic.StringValueFactory;
import io.setl.ls.proto.Info;
import io.setl.ls.proto.ProposeTransferSet;

public class TestProtobuf {

  @Test
  public void test() {
    TestContext.get().setStructureDepth(10);
    FactoryRepository repository = TestContext.get().getFactories();
    repository.addFactory(StringValueFactory.UPPERCASE);
    ProposeTransferSet set = TestContext.get().create(ProposeTransferSet.class, ValueType.RANDOM);
    System.out.println(set);

    Info info = TestContext.get().create(Info.class, ValueType.RANDOM);
    System.out.println(info);
  }

}
