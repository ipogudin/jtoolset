package memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jtoolset.memory.StandardTypeUtils;

import org.junit.Before;
import org.junit.Test;

public class SizeOfStandardTypesTest {

  private StandardTypeUtils standardTypeUtils;
  
  @Before
  public void setUp() {
    standardTypeUtils = new StandardTypeUtils();
  }
  
  @Test
  public void sizeOfString() {
    String s = "12345";
    assertTrue(standardTypeUtils.isSupportedType(s.getClass()));
    //The value has been calculated with according to String structure.
    //TODO must be validated on many platforms.
    assertEquals(38, standardTypeUtils.size(s));
  }
  
}
