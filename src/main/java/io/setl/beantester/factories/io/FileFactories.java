package io.setl.beantester.factories.io;


import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.setl.beantester.TestContext;
import io.setl.beantester.factories.ValueFactoryRepository;

public class FileFactories {

  private static File generateTempFile() {
    return generateTempPath().toFile();
  }


  private static Path generateTempPath() {
    try {
      Path path = Files.createTempFile("bean-tester-file-", ".txt");
      Files.delete(path);
      return path;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }


  public static void load(TestContext context, ValueFactoryRepository repository) {
    repository.addFactory(File.class, FileFactories::generateTempFile);
    repository.addFactory(Path.class, FileFactories::generateTempPath);
  }

}
