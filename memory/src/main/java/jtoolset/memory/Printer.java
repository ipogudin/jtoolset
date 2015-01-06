package jtoolset.memory;

import java.io.PrintStream;
import java.util.Arrays;

public class Printer implements ObjectVisitor {
  
  private final PrintStream out;
  
  public Printer(PrintStream out) {
    super();
    this.out = out;
  }

  public Printer() {
    this(System.out);
  }

  @Override
  public void visit(ObjectMeta objectMeta, Object object, int level)
      throws IllegalArgumentException, IllegalAccessException {
    char[] chars = new char[level*2];
    Arrays.fill(chars, ' ');
    
    String valueInfo = String.valueOf(object);
    
    out.println(String.format("%s%d %s (%s) = %1.24s", new String(chars), objectMeta.getOffset(), objectMeta.getName(),
        objectMeta.getType().getCanonicalName(), valueInfo));
  }

}
