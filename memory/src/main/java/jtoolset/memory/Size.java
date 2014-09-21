package jtoolset.memory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jtoolset.commons.UnsafeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Size {
  
  private static final Logger logger = LoggerFactory.getLogger(Size.class);
  
  private static final Map<Class<?>, Class<?>> primitiveTypeToClass = new ConcurrentHashMap<>();

  private static final Unsafe unsafe = UnsafeHelper.get();
  private static final SizeOfTraverseHook defaultTraverseHook = new SizeOfPrinter();
  
  static {
    primitiveTypeToClass.put(Boolean.TYPE, Boolean.class);
    primitiveTypeToClass.put(Byte.TYPE, Byte.class);
    primitiveTypeToClass.put(Character.TYPE, Character.class);
    primitiveTypeToClass.put(Short.TYPE, Short.class);
    primitiveTypeToClass.put(Integer.TYPE, Integer.class);
    primitiveTypeToClass.put(Long.TYPE, Long.class);
    primitiveTypeToClass.put(Float.TYPE, Float.class);
    primitiveTypeToClass.put(Double.TYPE, Double.class);
  }
  
  public static boolean isPrimitiveType(Class<?> type) {
    return primitiveTypeToClass.containsKey(type);
  }
  
  public static long sizeOfPrimitive(Class<?> type) {
    if (type == Boolean.TYPE) {
      return 1;
    }
    try {
      return primitiveTypeToClass.get(type).getField("SIZE").getInt(null) / 8;
    } catch (IllegalArgumentException| IllegalAccessException | NoSuchFieldException | SecurityException e) {
      logger.error("Can not get size of a primitive type", e);
    }
    return 0;
  }
  
  public static boolean isArrayOfPrimitives(Class<?> type) {
    return type.isArray() && primitiveTypeToClass.containsKey(type.getComponentType());
  }
  
  public static long sizeOfArrayOfPrimitives(Object o) {
    return sizeOfPrimitive(o.getClass().getComponentType()) * (Array.getLength(o));
  }

  public static long of(Object masterObject, SizeOfTraverseHook traverseHook) {
    long offset = 0;
    long typeSize = 0;
    for (Field field : masterObject.getClass().getDeclaredFields()) {
      try {
        offset = unsafe.objectFieldOffset(field);
        traverseHook.notifyAboutObject(offset, field,
            masterObject);
      } catch (IllegalArgumentException | IllegalAccessException
          | NullPointerException e) {
      }
      
      
      if (isPrimitiveType(field.getType())) {
        typeSize += sizeOfPrimitive(field.getType());
      }
      else {
        Object o = null;
        boolean accessible = field.isAccessible();
        try {
          field.setAccessible(true);
          o = field.get(masterObject);
          typeSize += of(o, traverseHook);
        } catch (IllegalArgumentException | IllegalAccessException  e) {}
        finally {
          field.setAccessible(accessible);
        }
        typeSize += of(o, traverseHook);
      }
    }
    return typeSize;
  }

  public static long of(Object masterObject) {
    return of(masterObject, defaultTraverseHook);
  }

}