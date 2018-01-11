/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.utils.arquivo.csv;

import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Leandro
 */
public class ArquivoCSVTest {
    
    public ArquivoCSVTest() {
    }

    @Test
    public void testTratarString() throws Exception {
        String ln = "\"\"\"sad;\";\"12\";\"\";\"273274363\";\" \";\"ODAIR JOSE MARQUES\"";
        List<String> list = ArquivoCSV.tratarString(ln,';','"');
        assertEquals(6, list.size());
        assertEquals("\"sad;", list.get(0));
        assertEquals("12", list.get(1));
        assertEquals("", list.get(2));
        assertEquals("273274363", list.get(3));
        assertEquals(" ", list.get(4));
        assertEquals("ODAIR JOSE MARQUES", list.get(5));
    }
    
    @Test(expected = ArquivoCSV.CSVException.class)
    public void testTratarStringError1() throws ArquivoCSV.CSVException {
        String ln = "\"\"\"\"sad;\";\"12\";\"\"";
        List<String> list = ArquivoCSV.tratarString(ln,';','"');
        assertEquals(6, list.size());
    }
    
}
