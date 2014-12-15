package jtoolset.memory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jtoolset.commons.UnsafeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Size {
  
  private static final Logger logger = LoggerFactory.getLogger(Size.class);
  
  private static final long OBJECT_HEADER_SIZE = 12;
  private static final long BOOLEAN_SIZE = 1;
  
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
        return alignment(OBJECT_HEADER_SIZE +BOOLEAN_SIZE);
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
      return BOOLEAN_SIZE;
    }
    try {
      return primitiveTypeToClass.get(type).getField("SIZE").getInt(null) / 8;
    } catch (IllegalArgumentException| IllegalAccessException | NoSuchFieldException | SecurityException e) {
      logger.error("Can not get size of a primitive type", e);
    }
    return 0;
  }
  
  public boolean isStandardType(Class<?> type) {
    if (traverseStandardTypes) return false;
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
  
  private long of(Object masterObject, FieldVisitor fieldVisitor, int level, Set<Object> visitedObjects) {
    if (visitedObjects.contains(masterObject)) {
      return 0;
    }
    visitedObjects.add(masterObject);
    
    List<Field> fieldsOrderedByOffset = new ArrayList<>();
    
    for (Field field: masterObject.getClass().getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers())) continue;
      fieldsOrderedByOffset.add(field);
    }
    
    Collections.sort(fieldsOrderedByOffset, new Comparator<Field>() {
      @Override
      public int compare(Field o1, Field o2) {
        long offset1 = unsafe.objectFieldOffset(o1);
        long offset2 = unsafe.objectFieldOffset(o2);
        return (int) (offset1 - offset2);
      }
    });
    
    
    long offset = 0;
    long size = 0;
    long lastFieldSize = 0;
    for (Field field : fieldsOrderedByOffset) {
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
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else if (isStandardType(field.getType())) {
          size += sizeOfStandardType(o);
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else if (isPrimitiveType(field.getType())) {
          lastFieldSize = sizeOfPrimitive(field.getType());
        }
        else if (isArrayOfPrimitives(field.getType())) {
          size += sizeOfArrayOfPrimitives(o);
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else {
          size += of(o, fieldVisitor, level + 1, visitedObjects);
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
      } catch (IllegalArgumentException | IllegalAccessException  e) {}
      finally {
        field.setAccessible(accessible);
      }
    }
    return alignment(offset + lastFieldSize) + size;
  }

  public static long alignment(long size) {
    long sizeWithoutRemainder = (size / Address.ADDRESS_SIZE) * Address.ADDRESS_SIZE;
    if (size - sizeWithoutRemainder > 0) {
      return sizeWithoutRemainder + Address.ADDRESS_SIZE;
    }
    return size;
  }
  
  public long of(Object masterObject) {
    return of(masterObject, defaultFieldVisitor, 0, new HashSet<Object>());
  }

}