package jtoolset.memory;

import java.lang.reflect.Field;

public class FieldPrinter implements FieldVisitor {

  @Override
  public void notifyAboutField(long offset, Field field, Object masterObject)
      throws IllegalArgumentException, IllegalAccessException {
    System.out.println(String.format("%d %s (%s)", offset, field.getName(),
        field.getType().getCanonicalName()));
  }

}
