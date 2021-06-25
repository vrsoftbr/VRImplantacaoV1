package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Matchers.anyInt;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrframework.classe.Conexao;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.dao.sistema.SistemaDAO;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosServiceTest {
    
    @Test
    public void testGetSistema() throws Exception {
        
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        BancoDadosDAO bancoDAO = mock(BancoDadosDAO.class);
        ConfiguracaoBaseDadosDAO cfgDAO = mock(ConfiguracaoBaseDadosDAO.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(sistemaDAO, bancoDAO, cfgDAO);

        List<SistemaVO> sistemas = new ArrayList<>();
        SistemaVO vo = new SistemaVO();
        vo.setId(1);
        vo.setNome("GETWAY");

        sistemas.add(vo);

        vo = new SistemaVO();
        vo.setId(2);
        vo.setNome("SYSPDV");
        sistemas.add(vo);

        when(sistemaDAO.getSistema()).thenReturn(sistemas);

        assertEquals(sistemas.size(), service.getSistema().size());
    }

    @Test
    public void testGetBancoDadosPorSistema() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        BancoDadosDAO bancoDAO = mock(BancoDadosDAO.class);
        ConfiguracaoBaseDadosDAO cfgDAO = mock(ConfiguracaoBaseDadosDAO.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(sistemaDAO, bancoDAO, cfgDAO);

        List<BancoDadosVO> bancosPorSistema = new ArrayList<>();
        BancoDadosVO vo = new BancoDadosVO();
        vo.setId(1);
        vo.setNome("FIREBIRD");

        bancosPorSistema.add(vo);

        vo = new BancoDadosVO();
        vo.setId(2);
        vo.setNome("SQLSERVER");
        bancosPorSistema.add(vo);

        when(bancoDAO.getBancoDadosPorSistema(0)).thenReturn(bancosPorSistema);

        assertEquals(bancosPorSistema.size(), service.getBancoDadosPorSistema(anyInt()).size());
    }

    @Test
    public void testExisteConexao() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        BancoDadosDAO bancoDAO = mock(BancoDadosDAO.class);
        ConfiguracaoBaseDadosDAO cfgDAO = mock(ConfiguracaoBaseDadosDAO.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(sistemaDAO, bancoDAO, cfgDAO);

        ConfiguracaoBaseDadosVO configuracaoVO = new ConfiguracaoBaseDadosVO();

        when(cfgDAO.existeConexao(configuracaoVO)).thenReturn(false);

        assertEquals(false, service.existeConexao(configuracaoVO));
    }

    @Test
    public void testSalvar() throws Exception {
        
        try (MockedStatic conexaoMock = Mockito.mockStatic(Conexao.class)) {
            SistemaDAO sistemaDAO = mock(SistemaDAO.class);
            BancoDadosDAO bancoDAO = mock(BancoDadosDAO.class);
            ConfiguracaoBaseDadosDAO cfgDAO = mock(ConfiguracaoBaseDadosDAO.class);
            
            ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(sistemaDAO, bancoDAO, cfgDAO);
            
            ConfiguracaoBaseDadosVO conexaoVO = new ConfiguracaoBaseDadosVO();
            conexaoVO.setId(0);
            
            service.salvar(conexaoVO);
            
            when(service.existeConexao(conexaoVO)).thenReturn(Boolean.FALSE);
            
            Mockito.verify(cfgDAO, Mockito.times(1)).inserir(conexaoVO);
            
            conexaoMock.close();
        }
    }
}
