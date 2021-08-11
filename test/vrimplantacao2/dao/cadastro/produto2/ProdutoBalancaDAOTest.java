package vrimplantacao2.dao.cadastro.produto2;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.MockSettings;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import static vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO.*;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;

@RunWith(MockitoJUnitRunner.class)
public class ProdutoBalancaDAOTest {
    
    public ProdutoBalancaDAOTest() {
    }
    
    private ProdutoBalancaDAO buildMock(final TipoConversao tipoConversao) throws Exception {
        ProdutoBalancaDAO dao = mock(ProdutoBalancaDAO.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ProdutoBalancaMap mp = new ProdutoBalancaMap(tipoConversao);
                
                mp.put(1, new ProdutoBalancaVO(1, "T1", "P", 10));
                mp.put(2, new ProdutoBalancaVO(2, "T2", "U", 9));
                mp.put(39, new ProdutoBalancaVO(39, "T3", "U", 8));
                mp.put(4, new ProdutoBalancaVO(4, "T4", "P", 7));
                mp.put(5, new ProdutoBalancaVO(5, "T5", "P", 6));
                
                return mp;
            }
        }).when(dao).getProdutosBalanca(); 
        
        return dao;
    }

    @Test
    public void conversaoListaSimples() throws Exception {
        ProdutoBalancaDAO dao = buildMock(TipoConversao.SIMPLES);      
        
        Map<Integer, ProdutoBalancaVO> values = dao.getProdutosBalanca();
        assertFalse(values.containsKey(10));
        assertTrue(values.containsKey(1));
        assertFalse(values.containsKey(25));
        assertTrue(values.containsKey(2));
        assertFalse(values.containsKey(390));
        assertTrue(values.containsKey(39));
        assertFalse(values.containsKey(45));
        assertTrue(values.containsKey(4));
        assertFalse(values.containsKey(58));
        assertTrue(values.containsKey(5));
        
        assertNull(values.get(10));
        assertEquals("T1", values.get(1).getDescricao());
        assertNull(values.get(25));
        assertEquals("T2", values.get(2).getDescricao());
        assertNull(values.get(390));
        assertEquals("T3", values.get(39).getDescricao());
        assertNull(values.get(45));
        assertEquals("T4", values.get(4).getDescricao());
        assertNull(values.get(58));
        assertEquals("T5", values.get(5).getDescricao());
        
    }
    
     @Test
    public void conversaoListaRemoverUltimoDigito() throws Exception {
        ProdutoBalancaDAO dao = buildMock(TipoConversao.REMOVER_DIGITO);      
        
        Map<Integer, ProdutoBalancaVO> values = dao.getProdutosBalanca();
        assertTrue(values.containsKey(10));
        assertFalse(values.containsKey(1));
        assertTrue(values.containsKey(25));
        assertFalse(values.containsKey(2));
        assertTrue(values.containsKey(390));
        assertFalse(values.containsKey(39));
        assertTrue(values.containsKey(45));
        assertFalse(values.containsKey(4));
        assertTrue(values.containsKey(58));
        assertFalse(values.containsKey(5));
        
        assertEquals("T1", values.get(10).getDescricao());
        assertNull(values.get(1));
        assertEquals("T2", values.get(25).getDescricao());
        assertNull(values.get(2));
        assertEquals("T3", values.get(390).getDescricao());
        assertNull(values.get(39));
        assertEquals("T4", values.get(45).getDescricao());
        assertNull(values.get(4));
        assertEquals("T5", values.get(58).getDescricao());
        assertNull(values.get(5));
        
    }
    
    @Test
    public void conversaoRemoverDigito() throws Exception {
        ProdutoBalancaDAO dao = buildMock(TipoConversao.REMOVER_DIGITO);      
        
        Map<Integer, ProdutoBalancaVO> values = dao.getProdutosBalanca();
        assertTrue(values.containsKey(10));
        assertFalse(values.containsKey(1));
        assertTrue(values.containsKey(25));
        assertFalse(values.containsKey(2));
        assertTrue(values.containsKey(390));
        assertFalse(values.containsKey(39));
        
    }
    
    @Test
    public void conversaoSimples() {
        final TipoConversao v = TipoConversao.SIMPLES;
        assertEquals(10, v.convert("10"));
        assertEquals(25, v.convert("25"));
        assertEquals(395, v.convert("395"));
        assertEquals(4567, v.convert("4567"));
        assertEquals(1908763, v.convert("1908763"));
        assertEquals(9999991, v.convert("9999991"));
        assertEquals(9999991, v.convert("9999991ASD"));
        assertEquals(-2, v.convert("SASD"));
    }
    
    @Test
    public void convertRemoverDigito() {
        final TipoConversao v = TipoConversao.REMOVER_DIGITO;
        assertEquals(1, v.convert("10"));
        assertEquals(2, v.convert("25"));
        assertEquals(39, v.convert("395"));
        assertEquals(456, v.convert("4567"));
        assertEquals(190876, v.convert("1908763"));
        assertEquals(999999, v.convert("9999991"));
        assertEquals(999999, v.convert("9999991ASD"));
        assertEquals(-2, v.convert("SASD"));
    }
    
}
