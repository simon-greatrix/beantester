package io.setl.beantester.factories;

import org.kohsuke.MetaInfServices;
import org.meanbean.factories.basic.BooleanFactory;
import org.meanbean.factories.basic.ByteFactory;
import org.meanbean.factories.basic.CharacterFactory;
import org.meanbean.factories.basic.DoubleFactory;
import org.meanbean.factories.basic.FloatFactory;
import org.meanbean.factories.basic.IntegerFactory;
import org.meanbean.factories.basic.LongFactory;
import org.meanbean.factories.basic.ShortFactory;
import java.util.random.RandomGenerator;

/**
 * Concrete FactoryCollectionPlugin that registers Factories that create Java primitives.
 *
 * @author Graham Williamson
 */
@MetaInfServices
public class PrimitiveFactoryPlugin implements FactoryCollectionPlugin {

  @Override
  public void initialize(FactoryCollection factoryCollection, RandomGenerator randomValueGenerator) {
    factoryCollection.addFactory(boolean.class, new BooleanFactory(randomValueGenerator));
    factoryCollection.addFactory(byte.class, new ByteFactory(randomValueGenerator));
    factoryCollection.addFactory(short.class, new ShortFactory(randomValueGenerator));
    factoryCollection.addFactory(int.class, new IntegerFactory(randomValueGenerator));
    factoryCollection.addFactory(long.class, new LongFactory(randomValueGenerator));
    factoryCollection.addFactory(float.class, new FloatFactory(randomValueGenerator));
    factoryCollection.addFactory(double.class, new DoubleFactory(randomValueGenerator));
    factoryCollection.addFactory(char.class, new CharacterFactory(randomValueGenerator));
    factoryCollection.addFactory(void.class, () -> null);
  }

}
