package memory;

import static org.junit.Assert.*;

import org.junit.Test;

import jtoolset.memory.Size;

public class SizeOfStandardTypesTest {

  @Test
  public void sizeOfByte() {
    assertEquals(1, Size.sizeOfPrimitive(String.class));
  }
  
}
