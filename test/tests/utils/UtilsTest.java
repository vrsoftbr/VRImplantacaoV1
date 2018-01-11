/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public class UtilsTest {
    
    public UtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testAcertoTexto1() {
        String valor = "@;\\/ Teste de campos $$ € ã Àá123456";
        
        assertEquals("@   TESTE DE CAMPOS    C A AA123456", Utils.acertarTexto(valor));        
        assertEquals("", Utils.acertarTexto(null));
    }

    @Test
    public void testAcertoTexto2() throws Exception{
        String valor = "@;\\/ Teste de campos $$ € ã Àá123456";
        
        assertEquals("@   TESTE DE CAMPOS    C A AA1", Utils.acertarTexto(valor, 30, "TESTES"));        
        assertEquals("TESTE NULO", Utils.acertarTexto(null, 30, "TESTE NULO"));
        assertEquals("", Utils.acertarTexto(null, 30, null));
    }
    
    @Test
    public void testAcertoTextoSemValorPadrao() throws Exception {
        String valor = "@;\\/ Teste de campos $$ € ã Àá123456";
        
        assertEquals("@   TESTE DE CAMPOS    C A AA1", Utils.acertarTexto(valor, 30));        
        assertEquals("", Utils.acertarTexto(null, 30));
    }
    
    @Test
    public void testTelCel() {
        assertEquals("1934567896", Utils.formataNumero("(19)3456-7896", 14));
        assertEquals("19912345678", Utils.formataNumero("(19)9-1234-5678", 14));
        assertEquals("1934567896", Utils.formataNumero("(19)345c6-7896AB", 14));
        assertEquals("", Utils.formataNumero(null, 14,""));
        assertEquals("", Utils.formataNumero("", 14,""));
    }
    
    @Test
    public void testFormataNumero0() {
        assertEquals("123456789012345", Utils.formataNumero("ABCDEFG1234567890ABC12345"));
        assertEquals("0", Utils.formataNumero(null));
        assertEquals("0", Utils.formataNumero("abcdefgrtekfgjldfg"));
        assertEquals("0", Utils.formataNumero(""));
    }
    
    @Test
    public void testFormataNumero1() {
        assertEquals("1234567890", Utils.formataNumero("ABCDEFG1234567890ABC12345", 10, "ISENTO"));
        assertEquals("ISENTO", Utils.formataNumero(null, 10, "ISENTO"));
        assertEquals("ISENTO", Utils.formataNumero("abcdefgrtekfgjldfg", 10, "ISENTO"));
        assertEquals("ISENTO", Utils.formataNumero("", 10, "ISENTO"));
    }
    
    @Test
    public void testFormataNumero2() {
        assertEquals("123456789012345", Utils.formataNumero("ABCDEFG1234567890ABC12345", "ISENTO"));
        assertEquals("ISENTO", Utils.formataNumero(null, "ISENTO"));
        assertEquals("ISENTO", Utils.formataNumero("abcdefgrtekfgjldfg", "ISENTO"));
        assertEquals("ISENTO", Utils.formataNumero("", "ISENTO"));
    }
    
    @Test
    public void testFormataNumero3() {
        assertEquals("1234567890", Utils.formataNumero("ABCDEFG1234567890ABC12345", 10));
        assertEquals("0", Utils.formataNumero(null, 10));
        assertEquals("0", Utils.formataNumero("abcdefgrtekfgjldfg", 10));
        assertEquals("0", Utils.formataNumero("", 10));
    }
    
    @Test
    public void testArredondarTrunc() {
        assertEquals(123d, Utils.truncar(123.459873d, 0), 0.000000001);
        assertEquals(123.4d, Utils.truncar(123.459873d, 1), 0.000000001);
        assertEquals(123.45d, Utils.truncar(123.459873d, 2), 0.000000001);
        assertEquals(123.459d, Utils.truncar(123.459873d, 3), 0.000000001);
        assertEquals(123.4598d, Utils.truncar(123.459873d, 4), 0.000000001);
        assertEquals(123.45987d, Utils.truncar(123.459873d, 5), 0.000000001);
        assertEquals(123.459873d, Utils.truncar(123.459873d, 6), 0.000000001);
        assertEquals(123.459873d, Utils.truncar(123.459873d, 7), 0.000000001);
        assertEquals(7.459, Utils.truncar(7.4598737812354651237812374d, 3), 0.000000001);
    }

    @Test
    public void testTruncar2() {
        assertEquals(123d, Utils.truncar2(123, 0), 0.000000001);
        assertEquals(123d, Utils.truncar2(123, 1), 0.000000001);
        assertEquals(123d, Utils.truncar2(123, 2), 0.000000001);
        assertEquals(123d, Utils.truncar2(123.459873d, 0), 0.000000001);
        assertEquals(123.4d, Utils.truncar2(123.459873d, 1), 0.000000001);
        assertEquals(123.45d, Utils.truncar2(123.459873d, 2), 0.000000001);
        assertEquals(123.459d, Utils.truncar2(123.459873d, 3), 0.000000001);
        assertEquals(123.4598d, Utils.truncar2(123.459873d, 4), 0.000000001);
        assertEquals(123.45987d, Utils.truncar2(123.459873d, 5), 0.000000001);
        assertEquals(123.459873d, Utils.truncar2(123.459873d, 6), 0.000000001);
        assertEquals(123.459873d, Utils.truncar2(123.459873d, 7), 0.000000001);
        assertEquals(7.459, Utils.truncar2(7.4598737812354651237812373452344d, 3), 0.000000001);
        
        assertEquals(-123d, Utils.truncar2(-123, 0), 0.000000001);
        assertEquals(-123d, Utils.truncar2(-123, 1), 0.000000001);
        assertEquals(-123d, Utils.truncar2(-123, 2), 0.000000001);
        assertEquals(-123d, Utils.truncar2(-123.459873d, 0), 0.000000001);
        assertEquals(-123.4d, Utils.truncar2(-123.459873d, 1), 0.000000001);
        assertEquals(-123.45d, Utils.truncar2(-123.459873d, 2), 0.000000001);
        assertEquals(-123.459d, Utils.truncar2(-123.459873d, 3), 0.000000001);
        assertEquals(-123.4598d, Utils.truncar2(-123.459873d, 4), 0.000000001);
        assertEquals(-123.45987d, Utils.truncar2(-123.459873d, 5), 0.000000001);
        assertEquals(-123.459873d, Utils.truncar2(-123.459873d, 6), 0.000000001);
        assertEquals(-123.459873d, Utils.truncar2(-123.459873d, 7), 0.000000001);
        assertEquals(-7.459, Utils.truncar2(-7.4598737812354651237812373452344d, 3), 0.000000001);
    }
    
    @Test
    public void testIsCnpjCpfValido() {
        
        /*assertFalse(Utils.isCnpjCpfValido(1l));
        assertFalse(Utils.isCnpjCpfValido(1234567891l));
        
        assertFalse(Utils.isCnpjCpfValido(11111111111l));
        assertFalse(Utils.isCnpjCpfValido(22222222222l));
        assertFalse(Utils.isCnpjCpfValido(33333333333l));
        assertFalse(Utils.isCnpjCpfValido(44444444444l));
        assertFalse(Utils.isCnpjCpfValido(55555555555l));
        assertFalse(Utils.isCnpjCpfValido(66666666666l));
        assertFalse(Utils.isCnpjCpfValido(77777777777l));
        assertFalse(Utils.isCnpjCpfValido(88888888888l));
        assertFalse(Utils.isCnpjCpfValido(99999999999l));
        
        assertFalse(Utils.isCnpjCpfValido(1045689700012l));
        assertFalse(Utils.isCnpjCpfValido(104568970001235554l));
        
        assertFalse(Utils.isCnpjCpfValido(11111111111111l));
        assertFalse(Utils.isCnpjCpfValido(22222222222222l));
        assertFalse(Utils.isCnpjCpfValido(33333333333333l));
        assertFalse(Utils.isCnpjCpfValido(44444444444444l));
        assertFalse(Utils.isCnpjCpfValido(55555555555555l));
        assertFalse(Utils.isCnpjCpfValido(66666666666666l));
        assertFalse(Utils.isCnpjCpfValido(77777777777777l));
        assertFalse(Utils.isCnpjCpfValido(88888888888888l));
        assertFalse(Utils.isCnpjCpfValido(99999999999999l));
        
        assertTrue(Utils.isCnpjCpfValido(12345678910l));
        assertTrue(Utils.isCnpjCpfValido(12123456000123l));*/
        
    }

}
