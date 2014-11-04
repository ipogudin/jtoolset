package memory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import jtoolset.memory.Size;

public class SizeOfPrimitiveTypesTest {
  
  private Size size;
  
  @Before
  public void setUp() {
    size = new Size();
  }

  @Test
  public void sizeOfByte() {
    assertEquals(1, size.sizeOfPrimitive(byte.class));
  }
  
  @Test
  public void sizeOfPritiveByteArray() {
    byte[] b = new byte[10];
    assertEquals(10, size.sizeOfArrayOfPrimitives(b));
  }
  
  @Test
  public void sizeOfPritiveBooleanArray() {
    boolean[] b = new boolean[10];
    assertEquals(10, size.sizeOfArrayOfPrimitives(b));
  }
  
  @Test
  public void sizeOfPrimitiveIntArray() {
    int[] i = new int[10];
    assertEquals(40, size.sizeOfArrayOfPrimitives(i));
  }
  
  @Test
  public void sizeOfPrimitiveLongArray() {
    long[] l = new long[10];
    assertEquals(80, size.sizeOfArrayOfPrimitives(l));
  }
  
  @Test
  public void arrayOfPrimitiveTypesShouldBeRecognized() {
    assertTrue(size.isArrayOfPrimitives(new int[0].getClass()));
    assertFalse(size.isArrayOfPrimitives(new Integer[0].getClass()));
  }
}


