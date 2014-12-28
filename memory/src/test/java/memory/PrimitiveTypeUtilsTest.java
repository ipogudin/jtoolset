package memory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import jtoolset.memory.PrimitiveTypeUtils;
import jtoolset.memory.Size;

public class PrimitiveTypeUtilsTest {
  
  private PrimitiveTypeUtils primitiveTypeUtils;
  
  @Before
  public void setUp() {
    primitiveTypeUtils = new PrimitiveTypeUtils();
  }

  @Test
  public void sizeOfByte() {
    assertEquals(1, primitiveTypeUtils.size(byte.class));
  }
  
  @Test
  public void sizeOfPritiveByteArray() {
    byte[] b = new byte[10];
    assertEquals(10, primitiveTypeUtils.sizeOfArray(b));
  }
  
  @Test
  public void sizeOfPritiveBooleanArray() {
    boolean[] b = new boolean[10];
    assertEquals(10, primitiveTypeUtils.sizeOfArray(b));
  }
  
  @Test
  public void sizeOfPrimitiveIntArray() {
    int[] i = new int[10];
    assertEquals(40, primitiveTypeUtils.sizeOfArray(i));
  }
  
  @Test
  public void sizeOfPrimitiveLongArray() {
    long[] l = new long[10];
    assertEquals(80, primitiveTypeUtils.sizeOfArray(l));
  }
  
  @Test
  public void arrayOfPrimitiveTypesShouldBeRecognized() {
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new int[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Integer[0].getClass()));
  }
}

