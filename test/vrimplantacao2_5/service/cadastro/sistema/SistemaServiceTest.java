package vrimplantacao2_5.service.cadastro.sistema;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.cadastro.sistema.SistemaDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaServiceTest {

    @Test
    public void testExisteSistema() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        
        when(sistemaDAO.existeSistema("CGA")).thenReturn(true);
        
        assertEquals(true, sistemaDAO.existeSistema("CGA"));
    }
    
    @Test
    public void testInserir() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        SistemaService sistemaService = new SistemaService(sistemaDAO, provider);
        
        SistemaVO sistemaVO = new SistemaVO();
        sistemaVO.setNome("CGA");
        
        sistemaService.inserir(sistemaVO);
        
        when(sistemaDAO.existeSistema(sistemaVO.getNome())).thenReturn(true);
        
        Mockito.verify(sistemaDAO, Mockito.times(1)).inserir(sistemaVO);
    }
    
    @Test
    public void testAlterar() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        SistemaService sistemaService = new SistemaService(sistemaDAO, provider);
        
        SistemaVO sistemaVO = new SistemaVO();
        sistemaVO.setNome("CGA");
        
        sistemaService.alterar(sistemaVO);
        
        Mockito.verify(sistemaDAO, Mockito.times(1)).alterar(sistemaVO);
    }

    @Test
    public void testConsultar() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        SistemaService sistemaService = new SistemaService(sistemaDAO, provider);
        
        List<SistemaVO> result = new ArrayList<>();
        SistemaVO sistemaVO = new SistemaVO();
        result.add(sistemaVO);
        
        when(sistemaDAO.consultar("CGA")).thenReturn(result);
        
        assertEquals(1, sistemaService.consultar("CGA").size());
    }
    
    @Test 
    public void testGetSistema() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        SistemaService sistemaService = new SistemaService(sistemaDAO, provider);
        
        List<SistemaVO> result = new ArrayList<>();
        SistemaVO sistemaVO = new SistemaVO();
        result.add(sistemaVO);
        
        when(sistemaDAO.getSistema()).thenReturn(result);
        
        assertEquals(1, sistemaService.getSistema().size());        
    }    
}
