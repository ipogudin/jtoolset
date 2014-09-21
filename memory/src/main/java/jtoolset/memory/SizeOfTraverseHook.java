package jtoolset.memory;

import java.lang.reflect.Field;

public interface SizeOfTraverseHook {

  void notifyAboutObject(long offset, Field field, Object masterObject)
      throws IllegalArgumentException, IllegalAccessException;

}
