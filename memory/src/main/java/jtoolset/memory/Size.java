package jtoolset.memory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jtoolset.commons.UnsafeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Size {
  
  private static final Logger logger = LoggerFactory.getLogger(Size.class);
  
  private static final Unsafe unsafe = UnsafeHelper.get();
  
  public static Size create(boolean traverseStandardTypes) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    PrimitiveTypeUtils primitiveTypeUtils = PrimitiveTypeUtils.create();
    WrapperTypeUtils wrapperTypeUtils = WrapperTypeUtils.create(primitiveTypeUtils);
    return new Size(traverseStandardTypes, primitiveTypeUtils, wrapperTypeUtils);
  }

  private final PrimitiveTypeUtils primitiveTypeUtils;
  private final WrapperTypeUtils wrapperTypeUtils;
  private final ObjectVisitor defaultFieldVisitor = new Printer();
  private final boolean traverseStandardTypes;
  
  private Size(boolean traverseStandardTypes, PrimitiveTypeUtils primitiveTypeUtils, WrapperTypeUtils wrapperTypeUtils) {
    this.traverseStandardTypes = traverseStandardTypes;
    this.primitiveTypeUtils = primitiveTypeUtils;
    this.wrapperTypeUtils = wrapperTypeUtils;
  }
  
  protected long of(Object masterObject, ObjectVisitor fieldVisitor, int level, Set<Object> visitedObjects) throws IllegalArgumentException, IllegalAccessException {
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
      offset = unsafe.objectFieldOffset(field);
      
      Object o = null;
      boolean accessible = field.isAccessible();
      try {
        field.setAccessible(true);
        o = field.get(masterObject);
        fieldVisitor.visit(new FieldObjectMeta(field), o, level);
        if (o == null) {
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else if (!traverseStandardTypes && wrapperTypeUtils.isSupportedType(field.getType())) {
          size += wrapperTypeUtils.size(o);
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else if (primitiveTypeUtils.isSupportedType(field.getType())) {
          lastFieldSize = primitiveTypeUtils.size(field.getType());
          //size of primitive type should not be summed because of its value is stored directly in object's field 
        }
        else if (primitiveTypeUtils.isSupportedArrayType(field.getType())) {
          size += primitiveTypeUtils.sizeOfArray(o);
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else if (field.getType().isArray()) {
          int arrayLength = Array.getLength(o);
          size += Address.ADDRESS_SIZE * arrayLength;
          for (int i = 0; i < arrayLength; i++) {
            Object item = Array.get(o, i);
            Class<?> clazz = field.getType().getComponentType();
            fieldVisitor.visit(new SimpleObjectMeta(clazz, "array item", i), item, level + 1);
            if (item != null) {
              size += of(item, fieldVisitor, level + 2, visitedObjects);
            }
          }
          
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else {
          size += of(o, fieldVisitor, level + 1, visitedObjects);
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
      }
      finally {
        field.setAccessible(accessible);
      }
    }
    return Address.alignment(offset + lastFieldSize) + size;
  }
  
  public <T> long of(Object masterObject) throws IllegalArgumentException, IllegalAccessException {
    return of(masterObject, defaultFieldVisitor, 0, new HashSet<Object>());
  }

}