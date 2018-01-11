/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class MathUtilsTest {
    
    public MathUtilsTest() {
    }

    /**
     * Test of trunc method, of class MathUtils.
     */
    @Test
    public void testTrunc() {
        System.out.print("MathUtilsTest.testTrunc...");
        assertEquals(123d, MathUtils.trunc(123, 0), 0.000000001);
        assertEquals(123d, MathUtils.trunc(123, 1), 0.000000001);
        assertEquals(123d, MathUtils.trunc(123, 2), 0.000000001);
        assertEquals(123d, MathUtils.trunc(123.459873d, 0), 0.000000001);
        assertEquals(123.4d, MathUtils.trunc(123.459873d, 1), 0.000000001);
        assertEquals(123.45d, MathUtils.trunc(123.459873d, 2), 0.000000001);
        assertEquals(123.459d, MathUtils.trunc(123.459873d, 3), 0.000000001);
        assertEquals(123.4598d, MathUtils.trunc(123.459873d, 4), 0.000000001);
        assertEquals(123.45987d, MathUtils.trunc(123.459873d, 5), 0.000000001);
        assertEquals(123.459873d, MathUtils.trunc(123.459873d, 6), 0.000000001);
        assertEquals(123.459873d, MathUtils.trunc(123.459873d, 7), 0.000000001);
        assertEquals(7.459, MathUtils.trunc(7.4598737812354651237812373452344d, 3), 0.000000001);
        assertEquals(0, MathUtils.trunc(6.238219720554E10, 3), 0.000000001);
        
        assertEquals(-123d, MathUtils.trunc(-123, 0), 0.000000001);
        assertEquals(-123d, MathUtils.trunc(-123, 1), 0.000000001);
        assertEquals(-123d, MathUtils.trunc(-123, 2), 0.000000001);
        assertEquals(-123d, MathUtils.trunc(-123.459873d, 0), 0.000000001);
        assertEquals(-123.4d, MathUtils.trunc(-123.459873d, 1), 0.000000001);
        assertEquals(-123.45d, MathUtils.trunc(-123.459873d, 2), 0.000000001);
        assertEquals(-123.459d, MathUtils.trunc(-123.459873d, 3), 0.000000001);
        assertEquals(-123.4598d, MathUtils.trunc(-123.459873d, 4), 0.000000001);
        assertEquals(-123.45987d, MathUtils.trunc(-123.459873d, 5), 0.000000001);
        assertEquals(-123.459873d, MathUtils.trunc(-123.459873d, 6), 0.000000001);
        assertEquals(-123.459873d, MathUtils.trunc(-123.459873d, 7), 0.000000001);
        assertEquals(-7.459, MathUtils.trunc(-7.4598737812354651237812373452344d, 3), 0.000000001);
        assertEquals(0, MathUtils.trunc(-6.238219720554E10, 3), 0.000000001);
        System.out.println("OK");
    }

    /**
     * Test of round method, of class MathUtils.
     */
    @Test
    public void testRound() {
        System.out.print("MathUtilsTest.testRound...");
        assertEquals(123d, MathUtils.round(123, 0), 0.000000001);
        assertEquals(123d, MathUtils.round(123, 1), 0.000000001);
        assertEquals(123d, MathUtils.round(123, 2), 0.000000001);
        assertEquals(123d, MathUtils.round(123.459873d, 0), 0.000000001);
        assertEquals(123.5d, MathUtils.round(123.459873d, 1), 0.000000001);
        assertEquals(123.46d, MathUtils.round(123.459873d, 2), 0.000000001);
        assertEquals(123.460d, MathUtils.round(123.459873d, 3), 0.000000001);
        assertEquals(123.4599d, MathUtils.round(123.459873d, 4), 0.000000001);
        assertEquals(123.45987d, MathUtils.round(123.459873d, 5), 0.000000001);
        assertEquals(123.459873d, MathUtils.round(123.459873d, 6), 0.000000001);
        assertEquals(123.459873d, MathUtils.round(123.459873d, 7), 0.000000001);
        assertEquals(7.460, MathUtils.round(7.4598737812354651237812373452344d, 3), 0.000000001);
        assertEquals(0, MathUtils.round(6.238219720554E10, 3), 0.000000001);
        
        assertEquals(-123d, MathUtils.round(-123, 0), 0.000000001);
        assertEquals(-123d, MathUtils.round(-123, 1), 0.000000001);
        assertEquals(-123d, MathUtils.round(-123, 2), 0.000000001);
        assertEquals(-123d, MathUtils.round(-123.459873d, 0), 0.000000001);
        assertEquals(-123.5d, MathUtils.round(-123.459873d, 1), 0.000000001);
        assertEquals(-123.46d, MathUtils.round(-123.459873d, 2), 0.000000001);
        assertEquals(-123.460d, MathUtils.round(-123.459873d, 3), 0.000000001);
        assertEquals(-123.4599d, MathUtils.round(-123.459873d, 4), 0.000000001);
        assertEquals(-123.45987d, MathUtils.round(-123.459873d, 5), 0.000000001);
        assertEquals(-123.459873d, MathUtils.round(-123.459873d, 6), 0.000000001);
        assertEquals(-123.459873d, MathUtils.round(-123.459873d, 7), 0.000000001);
        assertEquals(-7.460, MathUtils.round(-7.4598737812354651237812373452344d, 3), 0.000000001); 
        assertEquals(0, MathUtils.round(-6.238219720554E10, 3), 0.000000001);
        System.out.println("OK");
   }
    
   @Test
   public void testGetDV() {
       System.out.print("MathUtilsTest.testGetDV...");
       assertEquals("10120", "1012" + MathUtils.getDV(1012));
       assertEquals("20435", "2043" + MathUtils.getDV(2043));
       assertEquals("20630", "2063" + MathUtils.getDV(2063));
       assertEquals("11614", "1161" + MathUtils.getDV(1161));
       assertEquals("183", "18" + MathUtils.getDV(18));
       assertEquals("20648", "2064" + MathUtils.getDV(2064));
       assertEquals("20958", "2095" + MathUtils.getDV(2095));
       assertEquals("20621", "2062" + MathUtils.getDV(2062));
       assertEquals("21687", "2168" + MathUtils.getDV(2168));
       assertEquals("23140", "2314" + MathUtils.getDV(2314));
       assertEquals("370", "37" + MathUtils.getDV(37));
       assertEquals("20265", "2026" + MathUtils.getDV(2026));
       assertEquals("13790", "1379" + MathUtils.getDV(1379));
       assertEquals("20605", "2060" + MathUtils.getDV(2060));
       assertEquals("20931", "2093" + MathUtils.getDV(2093));
       assertEquals("10111", "1011" + MathUtils.getDV(1011));
       assertEquals("21075", "2107" + MathUtils.getDV(2107));
       assertEquals("23205", "2320" + MathUtils.getDV(2320));
       assertEquals("6700", "670" + MathUtils.getDV(670));
       assertEquals("23213", "2321" + MathUtils.getDV(2321));
       assertEquals("20923", "2092" + MathUtils.getDV(2092));
       assertEquals("5363", "536" + MathUtils.getDV(536));
       assertEquals("6025", "602" + MathUtils.getDV(602));
       System.out.println("OK");
   }
    
}
