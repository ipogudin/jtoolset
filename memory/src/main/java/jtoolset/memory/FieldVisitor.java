package jtoolset.memory;

import java.lang.reflect.Field;

public interface FieldVisitor {

  void notifyAboutField(long offset, Field field, Object masterObject)
      throws IllegalArgumentException, IllegalAccessException;

}
