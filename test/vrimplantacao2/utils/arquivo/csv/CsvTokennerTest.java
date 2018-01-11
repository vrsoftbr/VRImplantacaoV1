/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.utils.arquivo.csv;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class CsvTokennerTest {
    
    public CsvTokennerTest() {
    }

    @Test
    public void testStart() {
        System.out.print("TokenerTest.testStart()...");
        CsvTokenner tokener = new CsvTokenner(';', '"');
        
        List<String> list = tokener.make("TESTE;AS \"TESTE\" ; \" ASDA\" ;\" TESTE2 \"ASDAS;\" BNMNB\" \" ;\" FGHJ\"ASDAS;\"QWERT\" ASDAS\"Y;\"1234\" A\"SS\" ;ASDA;\"lkjh\"  ;\"FINAL  ");
        
        assertEquals(11, list.size());
        assertEquals("TESTE", list.get(0));
        assertEquals("AS \"TESTE\" ", list.get(1));
        assertEquals(" \" ASDA\" ", list.get(2));
        assertEquals(" TESTE2 ASDAS", list.get(3));
        assertEquals(" BNMNB \" ", list.get(4));
        assertEquals(" FGHJASDAS", list.get(5));
        assertEquals("QWERT ASDAS\"Y", list.get(6));
        assertEquals("1234 A\"SS\" ", list.get(7));
        assertEquals("ASDA", list.get(8));
        assertEquals("lkjh  ", list.get(9));
        assertEquals("FINAL  ", list.get(10));
                
        list = tokener.make("TESTE;AS \"TESTE\" ; \" ASDA\" ;\" TESTE2 \"ASDAS;\" BNMNB\" \" ;\" FGHJ\"ASDAS;\"QWERT\" ASDAS\"Y;\"1234\" A\"SS\" ;ASDA;\"lkjh\"  ;\"FINAL  ");
        
        assertEquals(11, list.size());
        assertEquals("TESTE", list.get(0));
        assertEquals("AS \"TESTE\" ", list.get(1));
        assertEquals(" \" ASDA\" ", list.get(2));
        assertEquals(" TESTE2 ASDAS", list.get(3));
        assertEquals(" BNMNB \" ", list.get(4));
        assertEquals(" FGHJASDAS", list.get(5));
        assertEquals("QWERT ASDAS\"Y", list.get(6));
        assertEquals("1234 A\"SS\" ", list.get(7));
        assertEquals("ASDA", list.get(8));
        assertEquals("lkjh  ", list.get(9));
        assertEquals("FINAL  ", list.get(10));
                
        list = tokener.make("TESTE;AS \"TESTE\" ; \" ASDA\" ;\" TESTE2 \"ASDAS;\" BNMNB\" \" ;\" FGHJ\"ASDAS;\"QWERT\" ASDAS\"Y;\"1234\" A\"SS\" ;ASDA;\"lkjh\"  ;");
        
        assertEquals(11, list.size());
        assertEquals("TESTE", list.get(0));
        assertEquals("AS \"TESTE\" ", list.get(1));
        assertEquals(" \" ASDA\" ", list.get(2));
        assertEquals(" TESTE2 ASDAS", list.get(3));
        assertEquals(" BNMNB \" ", list.get(4));
        assertEquals(" FGHJASDAS", list.get(5));
        assertEquals("QWERT ASDAS\"Y", list.get(6));
        assertEquals("1234 A\"SS\" ", list.get(7));
        assertEquals("ASDA", list.get(8));
        assertEquals("lkjh  ", list.get(9));
        assertEquals("", list.get(10));
        System.out.println("OK");
    }
    
}
