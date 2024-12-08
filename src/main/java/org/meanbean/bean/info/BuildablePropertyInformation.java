package org.meanbean.bean.info;

import java.lang.reflect.Type;

import org.meanbean.util.ReadMethod;
import org.meanbean.util.WriteMethod;

public class BuildablePropertyInformation implements PropertyInformation {

  private final WriteMethod buildMethod;

  private final String name;

  private final ReadMethod readMethod;

  private final WriteMethod writeMethod;


  BuildablePropertyInformation(WriteMethod buildMethod, String name, ReadMethod readMethod, WriteMethod writeMethod) {
    this.buildMethod = buildMethod;
    this.name = name;
    this.readMethod = readMethod;
    this.writeMethod = writeMethod;
  }


  public WriteMethod getBuildMethod() {
    return buildMethod;
  }


  @Override
  public String getName() {
    return name;
  }


  @Override
  public ReadMethod getReadMethod() {
    return null;
  }


  @Override
  public Type getReadMethodReturnType() {
    return readMethod != null ? readMethod.getGenericReturnType() : null;
  }


  @Override
  public WriteMethod getWriteMethod() {
    return writeMethod;
  }


  @Override
  public Type getWriteMethodParameterType() throws IllegalArgumentException {
    return writeMethod != null ? writeMethod.getGenericValueType() : null;
  }


  @Override
  public boolean isNullable() {
    return false; // TODO
  }


  @Override
  public boolean isReadable() {
    return readMethod != null;
  }


  @Override
  public boolean isReadableWritable() {
    return isReadable() && isWritable();
  }


  @Override
  public boolean isWritable() {
    return writeMethod != null;
  }

}
