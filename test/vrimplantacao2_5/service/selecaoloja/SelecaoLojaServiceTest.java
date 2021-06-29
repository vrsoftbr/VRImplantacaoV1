package vrimplantacao2_5.service.selecaoloja;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class SelecaoLojaServiceTest {
    
    @Test
    public void testConsultar() throws Exception {
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        SelecaoLojaService service = new SelecaoLojaService(configuracaoDAO);
        
        List<ConfiguracaoBaseDadosVO> result = new ArrayList<>();
        ConfiguracaoBaseDadosVO configuracaoVO = new ConfiguracaoBaseDadosVO();
        configuracaoVO.setId(1);
        configuracaoVO.setDescricao("CONEXAO SYSPDV - 192.168.0.111");
        result.add(configuracaoVO);
        
        configuracaoVO = new ConfiguracaoBaseDadosVO();
        configuracaoVO.setId(2);
        configuracaoVO.setDescricao("CONEXAO SYSPDV - 192.168.0.112");
        result.add(configuracaoVO);
        
        when(configuracaoDAO.consultar()).thenReturn(result);
        
        List<ConfiguracaoBaseDadosVO> conexoes = service.consultar();
        
        assertEquals(result.size(), conexoes.size());
    }
    
    @Test
    public void testGetLojaMapeada() throws Exception {
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        SelecaoLojaService service = new SelecaoLojaService(configuracaoDAO);
        
        List<ConfiguracaoBancoLojaVO> result = new ArrayList<>();
        
        ConfiguracaoBancoLojaVO mapaLojaVO = new ConfiguracaoBancoLojaVO();
        mapaLojaVO.setId(1);
        mapaLojaVO.setIdLojaOrigem("1");
        mapaLojaVO.setIdLojaVR(2);
        result.add(mapaLojaVO);
        
        mapaLojaVO = new ConfiguracaoBancoLojaVO();
        mapaLojaVO.setId(2);
        mapaLojaVO.setIdLojaOrigem("2");
        mapaLojaVO.setIdLojaVR(1);
        result.add(mapaLojaVO);
        
        when(configuracaoDAO.getLojaMapeada(anyInt())).thenReturn(result);
        
        List<ConfiguracaoBancoLojaVO> lojas = service.getLojaMapeada(anyInt());
        
        assertEquals(result.size(), lojas.size());
    }
}
