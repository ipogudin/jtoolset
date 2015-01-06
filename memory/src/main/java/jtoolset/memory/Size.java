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
  
  private static final ObjectVisitor PRINTER = new Printer();
  private static final ObjectVisitor DUMMY = new Dummy();
  
  public static Size create(boolean traverseStandardTypes) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    PrimitiveTypeUtils primitiveTypeUtils = PrimitiveTypeUtils.create();
    WrapperTypeUtils wrapperTypeUtils = WrapperTypeUtils.create(primitiveTypeUtils);
    return new Size(traverseStandardTypes, primitiveTypeUtils, wrapperTypeUtils);
  }

  private final PrimitiveTypeUtils primitiveTypeUtils;
  private final WrapperTypeUtils wrapperTypeUtils;
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
    
    List<ObjectMeta> objectMetaListOrderedByOffset = new ArrayList<>();
    
    if ((!wrapperTypeUtils.isSupportedType(masterObject.getClass())) || 
    (wrapperTypeUtils.isSupportedType(masterObject.getClass()) && traverseStandardTypes)) {
      for (Field field: masterObject.getClass().getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers())) continue;
        objectMetaListOrderedByOffset.add(new FieldObjectMeta(field, masterObject));
      }
    }
    
    Collections.sort(objectMetaListOrderedByOffset, new Comparator<ObjectMeta>() {
      @Override
      public int compare(ObjectMeta o1, ObjectMeta o2) {
        return (int) (o1.getOffset()- o2.getOffset());
      }
    });
    
    if (objectMetaListOrderedByOffset.isEmpty() && 
        (masterObject.getClass().isArray() || wrapperTypeUtils.isSupportedType(masterObject.getClass()))) {
      objectMetaListOrderedByOffset.add(new SimpleObjectMeta(
          masterObject.getClass(), 
          "", 
          0l, 
          masterObject,
          true));
    }
    
    long offset = 0;
    long size = 0;
    long lastFieldSize = 0;
    for (ObjectMeta currentObjectMeta: objectMetaListOrderedByOffset) {
      offset = currentObjectMeta.getOffset();
      
      Object o = currentObjectMeta.getValue();
      if (!currentObjectMeta.isHidden()) {
        fieldVisitor.visit(currentObjectMeta, o, level);
      }
      if (o == null) {
        lastFieldSize = Address.JVM_ADDRESS_SIZE;
      }
      else if (!traverseStandardTypes && wrapperTypeUtils.isSupportedType(currentObjectMeta.getType())) {
        size += wrapperTypeUtils.size(o);
        lastFieldSize = Address.JVM_ADDRESS_SIZE;
      }
      else if (primitiveTypeUtils.isSupportedType(currentObjectMeta.getType())) {
        lastFieldSize = primitiveTypeUtils.size(currentObjectMeta.getType());
        //size of primitive type should not be summed because of its value is stored directly in object's field 
      }
      else if (primitiveTypeUtils.isSupportedArrayType(currentObjectMeta.getType())) {
        size += primitiveTypeUtils.sizeOfArray(o);
        lastFieldSize = Address.JVM_ADDRESS_SIZE;
      }
      else if (currentObjectMeta.getType().isArray()) {
        int arrayLength = Array.getLength(o);
        size += Address.ARRAY_HEADER_SIZE + Address.ADDRESS_SIZE * arrayLength;
        for (int i = 0; i < arrayLength; i++) {
          Object item = Array.get(o, i);
          Class<?> clazz = currentObjectMeta.getType().getComponentType();
          fieldVisitor.visit(new SimpleObjectMeta(clazz, "array item", i, o, false), item, level + 1);
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
    return Address.alignment(offset + lastFieldSize) + size;
  }
  
  public <T> long of(Object masterObject) throws IllegalArgumentException, IllegalAccessException {
    return of(masterObject, PRINTER);
  }
  
  public <T> long of(Object masterObject, ObjectVisitor visitor) throws IllegalArgumentException, IllegalAccessException {
    return of(masterObject, visitor, 0, new HashSet<Object>());
  }

}