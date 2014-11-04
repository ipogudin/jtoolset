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
  private static final FieldVisitor defaultTraverseHook = new FieldPrinter();
  
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
  
  public static boolean isStandardType(Class<?> type) {
    return false;
  }
  
  public static long sizeOfStandardType(Object o) {
    return 0;
  }
  
  public static boolean isArrayOfPrimitives(Class<?> type) {
    return type.isArray() && primitiveTypeToClass.containsKey(type.getComponentType());
  }
  
  public static long sizeOfArrayOfPrimitives(Object o) {
    long size = Address.ADDRESS_SIZE;
    if (o != null) {
      size += sizeOfPrimitive(o.getClass().getComponentType()) * (Array.getLength(o));
    }
    return size;
  }

  public static long of(Object masterObject, FieldVisitor fieldVisitor) {
    return of(masterObject, fieldVisitor, 0);
  }
  
  private static long of(Object masterObject, FieldVisitor fieldVisitor, int level) {
    long offset = 0;
    long size = 0;
    for (Field field : masterObject.getClass().getDeclaredFields()) {
      try {
        offset = unsafe.objectFieldOffset(field);
        fieldVisitor.notifyAboutField(offset, field, masterObject, level);
      } catch (IllegalArgumentException | IllegalAccessException
          | NullPointerException e) {
      }
      
      Object o = null;
      boolean accessible = field.isAccessible();
      try {
        field.setAccessible(true);
        o = field.get(masterObject);
        if (isPrimitiveType(field.getType())) {
          size += sizeOfPrimitive(field.getType());
        }
        else if (isArrayOfPrimitives(field.getType())) {
          size += sizeOfArrayOfPrimitives(o);
        }
        else if (o == null) {
          size = Address.ADDRESS_SIZE;
        }
        else {
          size += of(o, fieldVisitor, level + 1);
        }
      } catch (IllegalArgumentException | IllegalAccessException  e) {}
      finally {
        field.setAccessible(accessible);
      }
    }
    return size;
  }

  public static long of(Object masterObject) {
    return of(masterObject, defaultTraverseHook);
  }

}