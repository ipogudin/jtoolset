package jtoolset.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jtoolset.commons.UnsafeHelper;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Address {
  
  private static final Logger logger = LoggerFactory.getLogger(Address.class);

  private static final Unsafe unsafe = UnsafeHelper.get();

  public static final long OBJECT_HEADER_SIZE = 12;
  
  public static final int ADDRESS32BIT = 4;
  public static final int ADDRESS64BIT = 8;
  public static final int JVM_ADDRESS_SIZE = calculateAddressSize();
  public static final int ADDRESS_SIZE = unsafe.addressSize();
  
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
  
  public static long alignment(long size) {
    long sizeWithoutRemainder = (size / Address.ADDRESS_SIZE) * Address.ADDRESS_SIZE;
    if (size - sizeWithoutRemainder > 0) {
      return sizeWithoutRemainder + Address.ADDRESS_SIZE;
    }
    return size;
  }
  
  /**
   * JVM address size depends on platform (32 or 64), 
   * JVM option UseCompressedOops and maybe something else.
   * The best way to calculate real address size is measure field offset.
   * @return
   */
  private static int calculateAddressSize() {
    int addressSize = 0;
    try {
      addressSize = 
          (int) (unsafe.objectFieldOffset(Sample.class.getDeclaredField("o2")) 
      - unsafe.objectFieldOffset(Sample.class.getDeclaredField("o1")));
    } catch (NoSuchFieldException | SecurityException e) {
      logger.error("Something bad just happened ", e);
    }
    assert(addressSize > 0);
    return addressSize;
  }

  private static class Sample {
    private Object o1;
    private Object o2;
  }
}
