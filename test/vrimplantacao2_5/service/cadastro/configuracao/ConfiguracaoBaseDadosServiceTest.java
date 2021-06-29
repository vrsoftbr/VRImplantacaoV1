package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrframework.classe.VRException;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.dao.sistema.SistemaDAO;
import vrimplantacao2_5.gui.componente.conexao.firebird.ConexaoFirebirdPanel;
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

        ConfiguracaoBaseDadosServiceProvider provider
                = mock(ConfiguracaoBaseDadosServiceProvider.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(
                sistemaDAO, bancoDAO,
                cfgDAO, provider);
        
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

        ConfiguracaoBaseDadosServiceProvider provider
                = mock(ConfiguracaoBaseDadosServiceProvider.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(
                sistemaDAO, bancoDAO,
                cfgDAO, provider);

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

        ConfiguracaoBaseDadosServiceProvider provider
                = mock(ConfiguracaoBaseDadosServiceProvider.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(
                sistemaDAO, bancoDAO,
                cfgDAO, provider);

        ConfiguracaoBaseDadosVO configuracaoVO = new ConfiguracaoBaseDadosVO();
        configuracaoVO.setId(0);
        configuracaoVO.setSchema("/home/cliente/rs/strieder/banco.fdb");
        configuracaoVO.setUsuario("SYSDBA");
        configuracaoVO.setHost("localhost");
        configuracaoVO.setSenha("masterkey");
        
        when(cfgDAO.existeConexao(configuracaoVO)).thenReturn(true);

        VRException assertThrows = assertThrows(VRException.class, () -> service.existeConexao(configuracaoVO));
        assertEquals("Já existe uma conexão cadastrada!", assertThrows.getMessage());
    }

    @Test
    public void testSalvarInserir() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        BancoDadosDAO bancoDAO = mock(BancoDadosDAO.class);
        ConfiguracaoBaseDadosDAO cfgDAO = mock(ConfiguracaoBaseDadosDAO.class);
        ConfiguracaoBaseDadosServiceProvider provider
                = mock(ConfiguracaoBaseDadosServiceProvider.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(
                sistemaDAO, bancoDAO,
                cfgDAO, provider);

        ConfiguracaoBaseDadosVO conexaoVO = new ConfiguracaoBaseDadosVO();
        conexaoVO.setId(0);

        service.salvar(conexaoVO);

        when(cfgDAO.existeConexao(conexaoVO)).thenReturn(true);

        Mockito.verify(cfgDAO, Mockito.times(1)).inserir(conexaoVO);
    }
    
    @Test
    public void testSalvarAlterar() throws Exception {
        SistemaDAO sistemaDAO = mock(SistemaDAO.class);
        BancoDadosDAO bancoDAO = mock(BancoDadosDAO.class);
        ConfiguracaoBaseDadosDAO cfgDAO = mock(ConfiguracaoBaseDadosDAO.class);
        ConfiguracaoBaseDadosServiceProvider provider
                = mock(ConfiguracaoBaseDadosServiceProvider.class);

        ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService(
                sistemaDAO, bancoDAO,
                cfgDAO, provider);

        ConfiguracaoBaseDadosVO conexaoVO = new ConfiguracaoBaseDadosVO();
        conexaoVO.setId(2);

        service.salvar(conexaoVO);

        when(cfgDAO.existeConexao(conexaoVO)).thenReturn(true);

        Mockito.verify(cfgDAO, Mockito.times(1)).alterar(conexaoVO);
    }
}
