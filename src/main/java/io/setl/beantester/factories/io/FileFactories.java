package io.setl.beantester.factories.io;


import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.setl.beantester.ValueFactory;
import io.setl.beantester.factories.FactoryRepository;
import io.setl.beantester.ValueType;

/**
 * Load the file factories.
 */
public class FileFactories {

  private static File generateTempFile(ValueType type) {
    return generateTempPath(type).toFile();
  }


  private static Path generateTempPath(ValueType type) {
    if (type == ValueType.PRIMARY) {
      return PRIMARY_PATH;
    } else if (type == ValueType.SECONDARY) {
      return SECONDARY_PATH;
    }
    try {
      Path path = Files.createTempFile("bean-tester-file-", ".txt");
      Files.delete(path);
      return path;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }


  /**
   * Load the file factories.
   *
   * @param repository the repository to load the factories into
   */
  public static void load(FactoryRepository repository) {
    repository.addFactory(new ValueFactory(File.class, FileFactories::generateTempFile));
    repository.addFactory(new ValueFactory(Path.class, FileFactories::generateTempPath));
  }


  private static final Path PRIMARY_PATH = generateTempPath(ValueType.RANDOM);


  private static final Path SECONDARY_PATH = generateTempPath(ValueType.RANDOM);


}
