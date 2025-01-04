package io.setl.beantester.info;

import java.util.Collection;

/**
 * An entity that can be described by a set of properties.
 *
 * @param <M> the model type
 */
public abstract class AbstractCreatorModel<M extends AbstractCreatorModel<M>> extends AbstractModel<M> implements BeanCreator<M> {

  protected BeanDescription owner;


  /** Default constructor. */
  public AbstractCreatorModel() {
    // do nothing
  }


  /**
   * Copy constructor.
   *
   * @param properties the properties to copy
   */
  protected AbstractCreatorModel(Collection<Property> properties) {
    super(properties);
  }


  @Override
  public void notifyChanged(Property property) {
    super.notifyChanged(property);
    if (owner != null) {
      owner.notifyChanged(property);
    }
  }


  @Override
  public void setOwner(BeanDescription newOwner) {
    if (owner != null && newOwner != null && owner != newOwner) {
      throw new IllegalStateException("Owner already set");
    }
    this.owner = newOwner;
  }

}
