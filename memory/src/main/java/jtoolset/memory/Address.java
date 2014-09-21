package jtoolset.memory;

import jtoolset.commons.UnsafeHelper;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Address {

  public static final int ADDRESS32BIT = 4;
  public static final int ADDRESS64BIT = 8;
  public static final int ADDRESS_SIZE = UnsafeHelper.get().addressSize();

  public static long get(Object o) {
    Object[] array = new Object[] { o };
    Unsafe unsafe = UnsafeHelper.get();

    long baseOffset = unsafe.arrayBaseOffset(Object[].class);
    long objectAddress;
    switch (ADDRESS_SIZE) {
    case ADDRESS32BIT:
      objectAddress = unsafe.getInt(array, baseOffset);
      break;
    case ADDRESS64BIT:
      objectAddress = unsafe.getLong(array, baseOffset);
      break;
    default:
      throw new Error("unsupported address size: " + ADDRESS_SIZE);
    }

    return objectAddress;
  }

}
