package vrimplantacao2_5.service.atualizador;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.atualizador.AtualizadorDAO;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author Desenvolvimento
 */
public class AtualizadorServiceTest {
    
    @Test
    public void testVerificarBancoDados() throws Exception {
        AtualizadorDAO atulizadorDAO = mock(AtualizadorDAO.class);        

        when(atulizadorDAO.verificarBancoDados(EBancoDados.FIREBIRD)).thenReturn(true);
        
        assertEquals(true, atulizadorDAO.verificarBancoDados(EBancoDados.FIREBIRD));
    }
    
    @Test
    public void testGetBancoDados() throws Exception {
        AtualizadorDAO atulizadorDAO = mock(AtualizadorDAO.class);  
        AtualizadorService atualizadorService = new AtualizadorService(atulizadorDAO);
        
        List<EBancoDados> result = new ArrayList<>();
        for (EBancoDados eBancoDados : EBancoDados.values()) {
            if (!atualizadorService.verificarBancoDados(eBancoDados)) {
                result.add(eBancoDados);
                when(!atualizadorService.verificarBancoDados(eBancoDados)).thenReturn(false);
            } else {
                when(atualizadorService.verificarBancoDados(eBancoDados)).thenReturn(true);
            }
        }
        
        assertEquals(result.size(), atualizadorService.getBancoDados().size());
    }
    
    @Test
    public void testVerificarSistema() throws Exception {
        AtualizadorDAO atualizadorDAO = mock(AtualizadorDAO.class);
        
        when(atualizadorDAO.verificarSistema(ESistema.GETWAY)).thenReturn(true);
        
        assertEquals(true, atualizadorDAO.verificarSistema(ESistema.GETWAY));
    }
    
    @Test
    public void testGetSistema() throws Exception {
        AtualizadorDAO atualizadorDAO = mock(AtualizadorDAO.class);
        AtualizadorService atualizadorService = new AtualizadorService(atualizadorDAO);
        
        List<ESistema> result = new ArrayList<>();
        for (ESistema eSistema : ESistema.values()) {
            if (atualizadorService.verificarSistema(eSistema)) {                
                result.add(eSistema);
                when(!atualizadorService.verificarSistema(eSistema)).thenReturn(false);
            } else {
                when(atualizadorService.verificarSistema(eSistema)).thenReturn(true);
            }
        }
        
        assertEquals(result.size(), atualizadorService.getSistema().size());
    }
    
    @Test
    public void testSalvarBancoDados() throws Exception {
        AtualizadorDAO atualizadorDAO = mock(AtualizadorDAO.class);
        AtualizadorService atualizadorService = new AtualizadorService(atualizadorDAO);
        
        atualizadorService.salvarBancoDados();
        
        Mockito.verify(atualizadorDAO, Mockito.times(1)).salvarBancoDados(EBancoDados.FIREBIRD);
    }
    
    @Test
    public void testSalvarSistema() throws Exception {
        AtualizadorDAO atualizadorDAO = mock(AtualizadorDAO.class);
        AtualizadorService atualizadorService = new AtualizadorService(atualizadorDAO);
        
        atualizadorService.salvarSistema();
        
        Mockito.verify(atualizadorDAO, Mockito.times(1)).salvarSistema(ESistema.GETWAY);
    }
    
}
