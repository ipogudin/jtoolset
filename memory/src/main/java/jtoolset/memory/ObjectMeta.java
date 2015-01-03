package jtoolset.memory;

public interface ObjectMeta {

  Class<?> getType();
  String getName();
  long getOffset();
  Object getValue() throws IllegalArgumentException, IllegalAccessException;
  boolean isHidden();

}
