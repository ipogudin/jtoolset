package jtoolset.memory;

public class SimpleObjectMeta implements ObjectMeta {
  
  private final Class<?> clazz;
  private final String name;
  private final long offset;
  private final Object value;
  private final boolean hidden;

  public SimpleObjectMeta(Class<?> clazz, String name, long offset, Object value, boolean hidden) {
    super();
    this.clazz = clazz;
    this.name = name;
    this.offset = offset;
    this.value = value;
    this.hidden = hidden;
  }

  @Override
  public Class<?> getType() {
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

  @Override
  public Object getValue() throws IllegalArgumentException, IllegalAccessException {
    return value;
  }

  @Override
  public boolean isHidden() {
    return hidden;
  }

}

