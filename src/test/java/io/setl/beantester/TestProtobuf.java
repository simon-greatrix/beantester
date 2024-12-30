package io.setl.beantester;

import org.junit.jupiter.api.Test;

import io.setl.beantester.info.Specs;
import io.setl.beantester.info.specs.Protobuf;
import io.setl.ls.proto.Manifest;

public class TestProtobuf {

  @Test
  void testManifest() {
    BeanVerifier.verify(Manifest.class, new Protobuf());
  }
}
