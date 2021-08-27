package vrimplantacao2_5.service.cadastro.unidade;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.cadastro.unidade.UnidadeDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;

/**
 *
 * @author Desenvolvimento
 */
public class UnidadeServiceTest {

    @Test
    public void testExisteUnidade() throws Exception {
        UnidadeDAO unidadeDAO = mock(UnidadeDAO.class);
        
        UnidadeVO vo = new UnidadeVO();
        vo.setNome("VR MATRIZ");
        
        when(unidadeDAO.existeUnidade(vo)).thenReturn(true);
        
        assertEquals(true, unidadeDAO.existeUnidade(vo));
    }
    
    @Test
    public void testInserir() throws Exception {
        UnidadeDAO unidadeDAO = mock(UnidadeDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UnidadeService unidadeService = new UnidadeService(unidadeDAO, provider);
        
        UnidadeVO vo = new UnidadeVO();
        vo.setNome("VR RECIFE");
        vo.setIdEstado(26);
        vo.setIdMunicipio(2611606);

        unidadeService.inserir(vo);
        
        when(unidadeDAO.existeUnidade(vo)).thenReturn(true);
        
        Mockito.verify(unidadeDAO, Mockito.times(1)).inserir(vo);        
    }
    
    @Test
    public void testAlterar() throws Exception {
        UnidadeDAO unidadeDAO = mock(UnidadeDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UnidadeService unidadeService = new UnidadeService(unidadeDAO, provider);
        
        UnidadeVO vo = new UnidadeVO();
        vo.setNome("VR RECIFE");
        vo.setIdEstado(26);
        vo.setIdMunicipio(2611606);

        unidadeService.alterar(vo);        
        
        Mockito.verify(unidadeDAO, Mockito.times(1)).alterar(vo);        
    }
    
    @Test
    public void testConsultar() throws Exception {
        UnidadeDAO unidadeDAO = mock(UnidadeDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UnidadeService unidadeService = new UnidadeService(unidadeDAO, provider);
        
        List<UnidadeVO> result = new ArrayList<>();
        UnidadeVO unidadeVO = new UnidadeVO();
        unidadeVO.setNome("VR");
        unidadeVO.setIdEstado(35);
        result.add(unidadeVO);
        
        when(unidadeDAO.consultar(unidadeVO)).thenReturn(result);
        
        assertEquals(1, unidadeService.consultar(unidadeVO).size());
    }

    @Test 
    public void testGetUnidades() throws Exception {
        UnidadeDAO unidadeDAO = mock(UnidadeDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UnidadeService unidadeService = new UnidadeService(unidadeDAO, provider);
        
        List<UnidadeVO> result = new ArrayList<>();
        UnidadeVO unidadeVO = new UnidadeVO();
        result.add(unidadeVO);
        
        when(unidadeDAO.getUnidades()).thenReturn(result);
        
        assertEquals(1, unidadeService.getUnidades().size());        
    }    
}
