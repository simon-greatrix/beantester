package io.setl.beantester.info;


import java.util.Map;
import java.util.function.Function;

/**
 * Specify to create a bean using the specified function. The function takes a map of parameter names to values and should create the bean accordingly.
 */
public interface BeanCreator<B extends BeanCreator<B> & Model<B>> extends
                                                                  Function<Map<String, Object>, Object>,
                                                                  Model<B> {

}
