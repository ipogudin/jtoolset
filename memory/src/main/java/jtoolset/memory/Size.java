package jtoolset.memory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.RuntimeErrorException;

import jtoolset.commons.UnsafeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Size {
  
  private static final Logger logger = LoggerFactory.getLogger(Size.class);
  
  private static final Unsafe unsafe = UnsafeHelper.get();
  private static final Map<Class<?>, Class<?>> primitiveTypeToClass = new ConcurrentHashMap<>();
  private static final Map<Class<?>, StandardTypeObjectMeasurer> standardTypes = new ConcurrentHashMap<>();
  
  static {
    primitiveTypeToClass.put(Boolean.TYPE, Boolean.class);
    primitiveTypeToClass.put(Byte.TYPE, Byte.class);
    primitiveTypeToClass.put(Character.TYPE, Character.class);
    primitiveTypeToClass.put(Short.TYPE, Short.class);
    primitiveTypeToClass.put(Integer.TYPE, Integer.class);
    primitiveTypeToClass.put(Long.TYPE, Long.class);
    primitiveTypeToClass.put(Float.TYPE, Float.class);
    primitiveTypeToClass.put(Double.TYPE, Double.class);
    
    standardTypes.put(Boolean.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(Byte.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(Character.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(Short.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(Integer.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(Long.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(Float.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(Double.class, new StandardTypeObjectMeasurer<Boolean>() {
      @Override
      public long sizeOfObject(Boolean object) {
        // TODO Auto-generated method stub
        return 0;
      }
    });
    standardTypes.put(String.class, new StandardTypeObjectMeasurer<String>() {
      @Override
      public long sizeOfObject(String object) {
        final long OBJECT_OVERHEAD = 28;
        try {
          return OBJECT_OVERHEAD + object.length() * primitiveTypeToClass.get(Character.TYPE).getField("SIZE").getInt(null) / 8;
        } catch (IllegalArgumentException | IllegalAccessException
            | NoSuchFieldException | SecurityException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  private final FieldVisitor defaultFieldVisitor = new FieldPrinter();
  private final boolean traverseStandardTypes;
  
  public Size() {
    this(false);
  }
  
  public Size(boolean traverseStandardTypes) {
    super();
    this.traverseStandardTypes = traverseStandardTypes;
  }

  public boolean isPrimitiveType(Class<?> type) {
    return primitiveTypeToClass.containsKey(type);
  }
  
  public long sizeOfPrimitive(Class<?> type) {
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
  
  public boolean isStandardType(Class<?> type) {
    if (!traverseStandardTypes) return false;
    return standardTypes.containsKey(type);
  }
  
  public long sizeOfStandardType(Object o) {
    return standardTypes.get(o.getClass()).sizeOfObject(o);
  }
  
  public boolean isArrayOfPrimitives(Class<?> type) {
    return type.isArray() && primitiveTypeToClass.containsKey(type.getComponentType());
  }
  
  public long sizeOfArrayOfPrimitives(Object o) {
    return sizeOfPrimitive(o.getClass().getComponentType()) * (Array.getLength(o));
  }
  
  private long of(Object masterObject, FieldVisitor fieldVisitor, int level) {
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
        if (o == null) {
          size += Address.ADDRESS_SIZE;
        }
        else if (isStandardType(field.getType())) {
          size += Address.ADDRESS_SIZE;
          size += sizeOfStandardType(o);
        }
        else if (isPrimitiveType(field.getType())) {
          size += sizeOfPrimitive(field.getType());
        }
        else if (isArrayOfPrimitives(field.getType())) {
          size += sizeOfArrayOfPrimitives(o);
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

  public long of(Object masterObject) {
    return of(masterObject, defaultFieldVisitor, 0);
  }

}