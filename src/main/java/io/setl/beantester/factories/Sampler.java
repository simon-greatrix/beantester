package io.setl.beantester.factories;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.random.RandomGenerator;

/** Helper methods to select an item at random from a collection. */
public class Sampler {

  /**
   * Returns an optional of a random element from the collection.
   *
   * @param random     random number generator
   * @param collection collection to select from
   * @param <T>        type of the elements in the collection
   *
   * @return an optional of a random element from the collection, or an empty optional if the collection is empty
   */
  public static <T> Optional<T> findFrom(RandomGenerator random, Collection<T> collection) {
    if (collection.isEmpty()) {
      return Optional.empty();
    }
    int index = random.nextInt(collection.size());
    if (collection instanceof List && collection instanceof RandomAccess) {
      return Optional.of(((List<T>) collection).get(index));
    }

    Iterator<T> iterator = collection.iterator();
    for (int i = 0; i < index; i++) {
      iterator.next();
    }
    return Optional.ofNullable(iterator.next());
  }


  /**
   * Returns a random element from the collection.
   *
   * @param random     random number generator
   * @param collection collection to select from
   *
   * @return a random element from the collection, or null if the collection is empty
   */
  public static <T> T getFrom(RandomGenerator random, Collection<T> collection) {
    if (collection.isEmpty()) {
      return null;
    }
    int index = random.nextInt(collection.size());
    if (collection instanceof List && collection instanceof RandomAccess) {
      return ((List<T>) collection).get(index);
    }

    Iterator<T> iterator = collection.iterator();
    for (int i = 0; i < index; i++) {
      iterator.next();
    }
    return iterator.next();
  }

}
