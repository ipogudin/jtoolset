package jtoolset.memory;

import java.lang.reflect.Field;

public interface FieldVisitor {

  void notifyAboutField(long offset, Field field, Object masterObject, int level)
      throws IllegalArgumentException, IllegalAccessException;

}
