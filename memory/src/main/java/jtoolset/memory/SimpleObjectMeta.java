package jtoolset.memory;

public class SimpleObjectMeta implements ObjectMeta {
  
  private final Class<?> clazz;
  private final String name;
  private final long offset;

  public SimpleObjectMeta(Class<?> clazz, String name, long offset) {
    super();
    this.clazz = clazz;
    this.name = name;
    this.offset = offset;
  }

  @Override
  public Class<?> getClazz() {
    return clazz;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public long getOffset() {
    return offset;
  }

}
