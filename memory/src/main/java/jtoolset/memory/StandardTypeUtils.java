package jtoolset.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardTypeUtils {
  
  private static final long OBJECT_HEADER_SIZE = 12;
  private static final long BOOLEAN_SIZE = 1;
  private static final long BYTE_SIZE = 1;
  private static final long CHARACTER_SIZE = 1;
  
  private final Map<Class<?>, StandardTypeObjectMeasurer> standardTypeMeasurers = new ConcurrentHashMap<>();

  public StandardTypeUtils() {
    standardTypeMeasurers.put(Boolean.class, new StandardTypeObjectMeasurer<Boolean>() {
      
      private final long size = Address.alignment(OBJECT_HEADER_SIZE + BOOLEAN_SIZE);
      
      @Override
      public long sizeOfObject(Boolean object) {
        return size;
      }
      
    });
    standardTypeMeasurers.put(Byte.class, new StandardTypeObjectMeasurer<Byte>() {
      
      private final long size = Address.alignment(OBJECT_HEADER_SIZE + BYTE_SIZE);
      
      @Override
      public long sizeOfObject(Byte object) {
        return size;
      }
    });
    standardTypeMeasurers.put(Character.class, new StandardTypeObjectMeasurer<Character>() {
      
      private final long size = Address.alignment(OBJECT_HEADER_SIZE + CHARACTER_SIZE);
      
      @Override
      public long sizeOfObject(Character object) {
        return size;
      }
    });
    standardTypeMeasurers.put(Short.class, new StandardTypeObjectMeasurer<Short>() {
      @Override
      public long sizeOfObject(Short object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypeMeasurers.put(Integer.class, new StandardTypeObjectMeasurer<Integer>() {
      @Override
      public long sizeOfObject(Integer object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypeMeasurers.put(Long.class, new StandardTypeObjectMeasurer<Long>() {
      @Override
      public long sizeOfObject(Long object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypeMeasurers.put(Float.class, new StandardTypeObjectMeasurer<Float>() {
      @Override
      public long sizeOfObject(Float object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypeMeasurers.put(Double.class, new StandardTypeObjectMeasurer<Double>() {
      @Override
      public long sizeOfObject(Double object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypeMeasurers.put(String.class, new StandardTypeObjectMeasurer<String>() {
      
      private PrimitiveTypeUtils primitiveTypes = new PrimitiveTypeUtils();
      
      @Override
      public long sizeOfObject(String object) {
        final long OBJECT_OVERHEAD = 28;
        return OBJECT_OVERHEAD + object.length() * primitiveTypes.size(Character.TYPE);
      }
    });
  }
  
  public boolean isSupportedType(Class<?> clazz) {
    return standardTypeMeasurers.containsKey(clazz);
  }
  
  public long size(Object o) {
    return standardTypeMeasurers.get(o.getClass()).sizeOfObject(o);
  }
  
}
