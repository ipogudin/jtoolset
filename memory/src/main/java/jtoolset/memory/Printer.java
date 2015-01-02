package jtoolset.memory;

import java.util.Arrays;

public class Printer implements ObjectVisitor {

  @Override
  public void visit(ObjectMeta objectMeta, Object object, int level)
      throws IllegalArgumentException, IllegalAccessException {
    char[] chars = new char[level*2];
    Arrays.fill(chars, ' ');
    
    String valueInfo = "";
    if (object == null) {
      valueInfo = "null";
    }
    
    System.out.println(String.format("%s%d %s (%s) %s", new String(chars), objectMeta.getOffset(), objectMeta.getName(),
        objectMeta.getClazz().getCanonicalName(), valueInfo));
  }

}
