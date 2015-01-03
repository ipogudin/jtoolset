package jtoolset.memory;

public class Dummy implements ObjectVisitor {

  @Override
  public void visit(ObjectMeta objectMeta, Object object, int level)
      throws IllegalArgumentException, IllegalAccessException {
    //ignore all invocations
  }

}
