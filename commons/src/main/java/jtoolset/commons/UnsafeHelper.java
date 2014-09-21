package jtoolset.commons;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeHelper {
  
  private static final Logger logger = LoggerFactory.getLogger(UnsafeHelper.class);

  private static Unsafe unsafe = null;

  static {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      unsafe = (sun.misc.Unsafe) f.get(null);
    } catch (Throwable  e) {
      logger.error("Getting unsafe instance", e);
    }
  }

  /**
   * Workaround for Unsafe access
   * 
   * @return an instance of Unsafe
   */
  public static Unsafe get() {
    return unsafe;
  }

}
