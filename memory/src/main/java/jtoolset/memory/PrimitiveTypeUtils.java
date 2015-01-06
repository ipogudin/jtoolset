package jtoolset.memory;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitiveTypeUtils {

  private static final Logger logger = LoggerFactory.getLogger(PrimitiveTypeUtils.class);
  
  private static final long BOOLEAN_SIZE = 1;
  
  public static PrimitiveTypeUtils create() {
    return new PrimitiveTypeUtils();
  }

  private final Map<Class<?>, Class<?>> primitiveTypeToClass = new ConcurrentHashMap<>();
  
  private PrimitiveTypeUtils() {
    primitiveTypeToClass.put(Boolean.TYPE, Boolean.class);
    primitiveTypeToClass.put(Byte.TYPE, Byte.class);
    primitiveTypeToClass.put(Character.TYPE, Character.class);
    primitiveTypeToClass.put(Short.TYPE, Short.class);
    primitiveTypeToClass.put(Integer.TYPE, Integer.class);
    primitiveTypeToClass.put(Long.TYPE, Long.class);
    primitiveTypeToClass.put(Float.TYPE, Float.class);
    primitiveTypeToClass.put(Double.TYPE, Double.class);
  }
  
  public long size(Class<?> clazz) {
    if (clazz == Boolean.TYPE) {
      return BOOLEAN_SIZE;
    }
    
    if (!primitiveTypeToClass.containsKey(clazz)) {
      throw new IllegalArgumentException(String.valueOf(clazz) + " is not a supported primitive class.");
    }
    
    try {
      return primitiveTypeToClass.get(clazz).getField("SIZE").getInt(null) / 8;
    } catch (IllegalArgumentException| IllegalAccessException | NoSuchFieldException | SecurityException e) {
      logger.error("Can not get size of a primitive type", e);
    }
    return 0;
  }
  
  public boolean isSupportedType(Class<?> clazz) {
    return primitiveTypeToClass.containsKey(clazz);
  }
  
  public long sizeOfArray(Object o) {
    return Address.ARRAY_HEADER_SIZE + size(o.getClass().getComponentType()) * (Array.getLength(o));
  }
  
  public boolean isSupportedArrayType(Class<?> clazz) {
    return clazz.isArray() && isSupportedType(clazz.getComponentType());
  }
}
