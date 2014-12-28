package jtoolset.memory;

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

  private final PrimitiveTypeUtils primitiveTypes = new PrimitiveTypeUtils();
  private final StandardTypeUtils standardTypes = new StandardTypeUtils();
  private final FieldVisitor defaultFieldVisitor = new FieldPrinter();
  private final boolean traverseStandardTypes;
  
  public Size() {
    this(false);
  }
  
  public Size(boolean traverseStandardTypes) {
    super();
    this.traverseStandardTypes = traverseStandardTypes;
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
        else if (traverseStandardTypes && standardTypes.isSupportedType(field.getType())) {
          size += standardTypes.size(o);
          lastFieldSize = Address.JVM_ADDRESS_SIZE;
        }
        else if (primitiveTypes.isSupportedType(field.getType())) {
          lastFieldSize = primitiveTypes.size(field.getType());
          //size of primitive type should not be summed because of its value is stored directly in object's field 
        }
        else if (primitiveTypes.isSupportedArrayType(field.getType())) {
          size += primitiveTypes.sizeOfArray(o);
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
    return Address.alignment(offset + lastFieldSize) + size;
  }
  
  public long of(Object masterObject) {
    return of(masterObject, defaultFieldVisitor, 0, new HashSet<Object>());
  }

}