package io.setl.beantester.factories.io;


import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.random.RandomGenerator;

import io.setl.beantester.factories.FactoryCollection;
import io.setl.beantester.factories.FactoryCollectionPlugin;

public class FileFactory implements FactoryCollectionPlugin {

  private File generateTempFile() {
    return generateTempPath().toFile();
  }


  private Path generateTempPath() {
    try {
      Path path = Files.createTempFile("mean-bean-file-factory-", ".txt");
      Files.delete(path);
      return path;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }


  @Override
  public void initialize(FactoryCollection factoryCollection, RandomGenerator randomValueGenerator) {
    factoryCollection.addFactory(File.class, this::generateTempFile);
    factoryCollection.addFactory(Path.class, this::generateTempPath);
  }

}
