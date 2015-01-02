package jtoolset.memory;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrapperTypeUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(Size.class);
  
  public static WrapperTypeUtils create(final PrimitiveTypeUtils primitiveTypeUtils) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    Map<Class<?>, WrapperTypeObjectMeasurer> wrapperTypeMeasurers = new HashMap<>();
    
    wrapperTypeMeasurers.put(Boolean.class, SimpleWrapperMeasures.create(Boolean.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(Byte.class, SimpleWrapperMeasures.create(Byte.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(Character.class, SimpleWrapperMeasures.create(Character.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(Short.class, SimpleWrapperMeasures.create(Short.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(Integer.class, SimpleWrapperMeasures.create(Short.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(Long.class, SimpleWrapperMeasures.create(Long.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(Float.class, SimpleWrapperMeasures.create(Float.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(Double.class, SimpleWrapperMeasures.create(Double.class, primitiveTypeUtils));
    wrapperTypeMeasurers.put(String.class, new WrapperTypeObjectMeasurer<String>() {
      
      private final long OBJECT_OVERHEAD = 28;
      
      @Override
      public long sizeOfObject(String object) {
        return OBJECT_OVERHEAD + object.length() * primitiveTypeUtils.size(Character.TYPE);
      }
    });
    
    return new WrapperTypeUtils(wrapperTypeMeasurers);
  }
  
  private final Map<Class<?>, WrapperTypeObjectMeasurer> wrapperTypeMeasurers = new ConcurrentHashMap<>();

  private WrapperTypeUtils(Map<Class<?>, WrapperTypeObjectMeasurer> wrapperTypeMeasurers) {
    this.wrapperTypeMeasurers.putAll(wrapperTypeMeasurers);
  }
  
  public boolean isSupportedType(Class<?> clazz) {
    return wrapperTypeMeasurers.containsKey(clazz);
  }
  
  public long size(Object o) {
    return wrapperTypeMeasurers.get(o.getClass()).sizeOfObject(o);
  }
}
