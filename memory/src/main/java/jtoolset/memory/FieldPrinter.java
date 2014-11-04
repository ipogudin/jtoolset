package jtoolset.memory;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FieldPrinter implements FieldVisitor {

  @Override
  public void notifyAboutField(long offset, Field field, Object masterObject, int level)
      throws IllegalArgumentException, IllegalAccessException {
    char[] chars = new char[level*2];
    Arrays.fill(chars, ' ');
    System.out.println(String.format("%s%d %s (%s)", new String(chars), offset, field.getName(),
        field.getType().getCanonicalName()));
  }

}
