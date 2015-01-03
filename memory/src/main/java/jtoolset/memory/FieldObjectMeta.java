package jtoolset.memory;

import java.lang.reflect.Field;

import jtoolset.commons.UnsafeHelper;

public class FieldObjectMeta implements ObjectMeta {
  
  private final Field field;
  private final Object masterObject;

  public FieldObjectMeta(final Field field, final Object masterObject) {
    super();
    this.field = field;
    this.masterObject = masterObject;
  }

  @Override
  public Class<?> getType() {
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

  @Override
  public Object getValue() throws IllegalArgumentException, IllegalAccessException {
    boolean accessible = field.isAccessible();
    try {
      field.setAccessible(true);
      return field.get(masterObject);
    }
    finally {
      field.setAccessible(accessible);
    }
  }

  @Override
  public boolean isHidden() {
    return false;
  }
  
}
