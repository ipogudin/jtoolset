package jtoolset.memory;

public interface ObjectMeta {

  Class<?> getClazz();
  String getName();
  long getOffset();
  
}
