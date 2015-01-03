package memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import jtoolset.memory.PrimitiveTypeUtils;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveTypeUtilsTest {
  
  private static final int ARRAY_LENGTH = new Random().nextInt(1000);
  private static final long BOOLEAN_SIZE = 1;
  
  private PrimitiveTypeUtils primitiveTypeUtils;
  
  @Before
  public void setUp() {
    primitiveTypeUtils = PrimitiveTypeUtils.create();
  }

  @Test
  public void sizeOfBoolean() {
    assertEquals(BOOLEAN_SIZE, primitiveTypeUtils.size(boolean.class));
  }
  
  @Test
  public void sizeOfByte() {
    assertEquals(Byte.SIZE / 8, primitiveTypeUtils.size(byte.class));
  }
  
  @Test
  public void sizeOfChar() {
    assertEquals(Character.SIZE / 8, primitiveTypeUtils.size(char.class));
  }
  
  @Test
  public void sizeOfShort() {
    assertEquals(Short.SIZE / 8, primitiveTypeUtils.size(short.class));
  }
  
  @Test
  public void sizeOfInt() {
    assertEquals(Integer.SIZE / 8, primitiveTypeUtils.size(int.class));
  }
  
  @Test
  public void sizeOfLong() {
    assertEquals(Long.SIZE / 8, primitiveTypeUtils.size(long.class));
  }
  
  @Test
  public void sizeOfFloat() {
    assertEquals(Float.SIZE / 8, primitiveTypeUtils.size(float.class));
  }
  
  @Test
  public void sizeOfDouble() {
    assertEquals(Double.SIZE / 8, primitiveTypeUtils.size(double.class));
  }
  
  @Test
  public void sizeOfPritiveBooleanArray() {
    boolean[] a = new boolean[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * BOOLEAN_SIZE, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void sizeOfPritiveByteArray() {
    byte[] a = new byte[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * Byte.SIZE / 8, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void sizeOfPritiveCharArray() {
    char[] a = new char[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * Character.SIZE / 8, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void sizeOfPritiveShortArray() {
    short[] a = new short[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * Short.SIZE / 8, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void sizeOfPrimitiveIntArray() {
    int[] a = new int[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * Integer.SIZE / 8, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void sizeOfPrimitiveLongArray() {
    long[] a = new long[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * Long.SIZE / 8, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void sizeOfPrimitiveFloatArray() {
    float[] a = new float[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * Float.SIZE / 8, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void sizeOfPrimitiveDoubleArray() {
    double[] a = new double[ARRAY_LENGTH];
    assertEquals(ARRAY_LENGTH * Double.SIZE / 8, primitiveTypeUtils.sizeOfArray(a));
  }
  
  @Test
  public void arrayOfPrimitiveTypesShouldBeRecognized() {
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new boolean[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Boolean[0].getClass()));
    
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new byte[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Byte[0].getClass()));
    
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new char[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Character[0].getClass()));
    
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new short[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Short[0].getClass()));
    
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new int[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Integer[0].getClass()));
    
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new long[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Long[0].getClass()));
    
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new float[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Float[0].getClass()));
    
    assertTrue(primitiveTypeUtils.isSupportedArrayType(new double[0].getClass()));
    assertFalse(primitiveTypeUtils.isSupportedArrayType(new Double[0].getClass()));
  }
}


