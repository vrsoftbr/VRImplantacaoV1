package vr.datastream;

import org.junit.Test;
import static org.junit.Assert.*;

public class DataListTest {
    
    public DataListTest() {
    }

    @Test
    public void criacaoEAcessoAosDados() {
        DataList lista = DataList.of(
                new String[]{"id", "descricao", "sigla", "outros"},
                new Object[][]{
                    {0, "UNIDADE", "UN"},
                    {1, "CAIXA", "CX"},
                    {4, "KILO", "KG"}
                }
        );
        
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
    
    @Test
    public void rowComMaisCamposQueOIndex() {        
        DataList lista = DataList.of(
                new String[]{"id", "descricao", "sigla", "outros"},
                new Object[][]{
                    {0, "UNIDADE", "UN"},
                    {1, "CAIXA"},
                    {4, "KILO", "KG", "OK", "informacao a mais"}
                }
        );
        
        DataRow row = lista.getRow(0);
        assertEquals(0, row.get("id"));
        assertEquals("UNIDADE", row.get("descricao"));
        assertEquals("UN", row.get("sigla"));
        assertNull(row.get("outros"));
        assertNull(row.get("naoexiste"));
        
        row = lista.getRow(2);
        assertEquals(4, row.get("id"));
        assertEquals("KILO", row.get("descricao"));
        assertEquals("KG", row.get("sigla"));
        assertEquals("OK", row.get("outros"));
        assertNull(row.get("naoexiste"));
        assertNull(row.get(4));
    }
    
    @Test
    public void rowComMenosCamposQueOIndex() {        
        DataList lista = DataList.of(
                new String[]{"id", "descricao", "sigla", "outros"},
                new Object[][]{
                    {0, "UNIDADE", "UN"},
                    {1, "CAIXA"},
                    {4, "KILO", "KG", "OK", "informacao a mais"}
                }
        );
        
        DataRow row = lista.getRow(1);
        assertEquals(1, row.get("id"));
        assertEquals("CAIXA", row.get("descricao"));
        assertNull(row.get("sigla"));
        assertNull(row.get("outros"));
        assertNull(row.get("naoexiste"));
        assertNull(row.get(4));
    }
}
