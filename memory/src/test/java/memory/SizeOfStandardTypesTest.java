package memory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import jtoolset.memory.Size;

public class SizeOfStandardTypesTest {

  private Size size;
  
  @Before
  public void setUp() {
    size = new Size(true);
  }
  
  @Test
  public void sizeOfString() {
    assertTrue(size.isStandardType(String.class));
  }
  
}
