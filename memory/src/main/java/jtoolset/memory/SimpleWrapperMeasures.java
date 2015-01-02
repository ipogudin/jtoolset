package jtoolset.memory;

public class SimpleWrapperMeasures<T> implements WrapperTypeObjectMeasurer<T> {

  private final long size;
  
  private SimpleWrapperMeasures(long size) {
    this.size = size;
  }

  @Override
  public long sizeOfObject(T object) {
    return size;
  }

  public static <T extends Class<?>> SimpleWrapperMeasures<T> create(final T wrapperClazz, final PrimitiveTypeUtils primitiveTypeUtils) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    return new SimpleWrapperMeasures<T>(
        Address.alignment(
            Address.OBJECT_HEADER_SIZE + 
            primitiveTypeUtils.size((Class<?>) wrapperClazz.getField("TYPE").get(wrapperClazz)))
        );
  }
}
