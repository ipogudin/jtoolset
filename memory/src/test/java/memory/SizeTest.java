package memory;

import static org.junit.Assert.*;

import org.junit.Test;

import jtoolset.memory.Size;

public class SizeTest {

  @Test
  public void sizeOfByte() {
    assertEquals(1, Size.sizeOfPrimitive(byte.class));
  }
  
  @Test
  public void sizeOfPritiveByteArray() {
    byte[] b = new byte[10];
    assertEquals(10, Size.sizeOfArrayOfPrimitives(b));
  }
  
  @Test
  public void sizeOfPritiveBooleanArray() {
    boolean[] b = new boolean[10];
    assertEquals(10, Size.sizeOfArrayOfPrimitives(b));
  }
  
  @Test
  public void sizeOfPrimitiveIntArray() {
    int[] i = new int[10];
    assertEquals(40, Size.sizeOfArrayOfPrimitives(i));
  }
  
  @Test
  public void sizeOfPrimitiveLongArray() {
    long[] l = new long[10];
    assertEquals(80, Size.sizeOfArrayOfPrimitives(l));
  }
  
  @Test
  public void arrayOfPrimitiveTypesShouldBeRecognized() {
    assertTrue(Size.isArrayOfPrimitives(new int[0].getClass()));
    assertFalse(Size.isArrayOfPrimitives(new Integer[0].getClass()));
  }
}


