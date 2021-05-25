package vr.datastream;

import org.junit.Test;
import static org.junit.Assert.*;

public class DataListStreamTest {
    
    @Test
    public void condensarLista() {
        
        DataList lista = DataList.of(
                new String[]{"id", "descricao", "sigla", "outros"},
                new Object[][]{
                    {0, "UNIDADE", "UN"},
                    {1, "CAIXA", "CX"},
                    {4, "KILO", "KG"}
                }
        );        
        
        DataList resultado = DataStream.of(lista).condense();
        
        DataRow row = lista.getRow(0);
        assertEquals(0, row.get("id"));
        assertEquals("UNIDADE", row.get("descricao"));
        assertEquals("UN", row.get("sigla"));
        
        row = lista.getRow(1);
        assertEquals(1, row.get("id"));
        assertEquals("CAIXA", row.get("descricao"));
        assertEquals("CX", row.get("sigla"));
        
        row = lista.getRow(2);
        assertEquals(4, row.get("id"));
        assertEquals("KILO", row.get("descricao"));
        assertEquals("KG", row.get("sigla"));
    }
    
}
