package vrimplantacao2_5.service.cadastro.bancodados;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.cadastro.bancodados.BancoDadosDAO;

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
}
