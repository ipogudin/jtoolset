package memory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import jtoolset.memory.Size;

public class SizeOfStandardTypesTest {

  private Size size;
  
  @Before
  public void setUp() {
    size = new Size(false);
  }
  
  @Test
  public void sizeOfString() {
    String s = "12345";
    assertTrue(size.isStandardType(s.getClass()));
    //The value has been calculated with according to String structure.
    //TODO must be validated on many platforms.
    assertEquals(38, size.sizeOfStandardType(s));
  }
  
}
