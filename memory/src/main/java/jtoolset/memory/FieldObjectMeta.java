package jtoolset.memory;

import java.lang.reflect.Field;

import jtoolset.commons.UnsafeHelper;

public class FieldObjectMeta implements ObjectMeta {
  
  private final Field field;

  public FieldObjectMeta(Field field) {
    super();
    this.field = field;
  }

  @Override
  public Class<?> getClazz() {
    return field.getType();
  }

  @Override
  public String getName() {
    return field.getName();
  }

  @Override
  public long getOffset() {
    return UnsafeHelper.get().objectFieldOffset(field);
  }

}
