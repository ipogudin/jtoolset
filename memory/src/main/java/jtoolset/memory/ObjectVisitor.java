package jtoolset.memory;

public interface ObjectVisitor {

  void visit(ObjectMeta objectMeta, Object object, int level)
      throws IllegalArgumentException, IllegalAccessException;

}
