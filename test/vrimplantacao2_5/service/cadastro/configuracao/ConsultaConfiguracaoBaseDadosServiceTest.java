package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaConfiguracaoBaseDadosServiceTest {
    
    @Test
    public void testConsulta() throws Exception {
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        ConsultaConfiguracaoBaseDadosService service = new ConsultaConfiguracaoBaseDadosService(configuracaoDAO);
        
        List<ConfiguracaoBaseDadosVO> result = new ArrayList<>();
        ConfiguracaoBaseDadosVO configuracaoVO = new ConfiguracaoBaseDadosVO();
        result.add(configuracaoVO);
        
        configuracaoVO = new ConfiguracaoBaseDadosVO();
        result.add(configuracaoVO);
        
        when(configuracaoDAO.consultar()).thenReturn(result);
        
        assertEquals(2, service.consultar().size());
    }
}
