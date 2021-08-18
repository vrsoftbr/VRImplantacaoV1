package vrimplantacao2_5.service.cadastro.bancodados;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.cadastro.bancodados.BancoDadosDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class BancoDadosServiceTest {

    @Test
    public void testExisteBancoDados() throws Exception {
        BancoDadosDAO bancoDadosDAO = mock(BancoDadosDAO.class);
        
        when(bancoDadosDAO.existeBancoDados("FIREBIRD")).thenReturn(true);
        
        assertEquals(true, bancoDadosDAO.existeBancoDados("FIREBIRD"));
    }
    
    @Test
    public void testInserir() throws Exception {
        BancoDadosDAO bancoDadosDAO = mock(BancoDadosDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        BancoDadosService bancoDadosService = new BancoDadosService(bancoDadosDAO, provider);
        
        BancoDadosVO bancoDadosVO = new BancoDadosVO();
        bancoDadosVO.setNome("FIREBIRD");
        
        bancoDadosService.inserir(bancoDadosVO);
        
        when(bancoDadosDAO.existeBancoDados(bancoDadosVO.getNome())).thenReturn(true);
        
        Mockito.verify(bancoDadosDAO, Mockito.times(1)).inserir(bancoDadosVO);
    }
}
