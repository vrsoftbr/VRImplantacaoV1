package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.enums.ESituacaoMigracao;
import vrimplantacao2_5.vo.enums.ETipoLoja;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaServiceTest {
    
    @Test
    public void testGetLojaVR() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        List<LojaVO> lojas = new ArrayList<>();
        LojaVO lojaVO = new LojaVO();
        
        lojaVO.setId(1);
        lojaVO.setDescricao("LOJA 01");
        lojas.add(lojaVO);
        
        lojaVO = new LojaVO();
        lojaVO.setId(2);
        lojaVO.setDescricao("LOJA 02");
        lojas.add(lojaVO);
        
        when(lojaDAO.carregar()).thenReturn(lojas);
        
        assertEquals(2, service.getLojaVR().size());
    }
    
    @Test
    public void testVerificaLojaOrigem() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        ConfiguracaoBaseDadosVO configuracaoBancoVO = new ConfiguracaoBaseDadosVO();
        ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        
        configuracaoBancoLojaVO.setIdLojaOrigem("1");
        configuracaoBancoVO.setConfiguracaoBancoLoja(configuracaoBancoLojaVO);
        
        when(configuracaoDAO.existeLojaMapeada(ETipoLoja.LOJA_ORIGEM.name(), configuracaoBancoVO)).thenReturn(Boolean.TRUE);
        
        VRException assertThrows = assertThrows(VRException.class, () -> 
                                            service.verificaLojaOrigem(configuracaoBancoVO));
        
        assertEquals("Loja Origem 1 já mapeada!", assertThrows.getMessage());
    }
    
    @Test
    public void testVerificaLojaDestino() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        ConfiguracaoBaseDadosVO configuracaoBancoVO = new ConfiguracaoBaseDadosVO();
        ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        
        configuracaoBancoLojaVO.setIdLojaVR(2);
        configuracaoBancoVO.setConfiguracaoBancoLoja(configuracaoBancoLojaVO);
        
        when(configuracaoDAO.existeLojaMapeada(ETipoLoja.LOJA_DESTINO.name(), configuracaoBancoVO)).thenReturn(Boolean.TRUE);
        
        VRException assertThrows = assertThrows(VRException.class, () -> 
                                            service.verificaLojaDestino(configuracaoBancoVO));
        
        assertEquals("Loja VR 2 já mapeada!", assertThrows.getMessage());
    }
    
    @Test
    public void testVerificaLojaMatriz() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        ConfiguracaoBaseDadosVO configuracaoBancoVO = new ConfiguracaoBaseDadosVO();
        ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        
        configuracaoBancoLojaVO.setLojaMatriz(true);
        configuracaoBancoVO.setConfiguracaoBancoLoja(configuracaoBancoLojaVO);
        
        when(configuracaoDAO.verificaLojaMatriz(configuracaoBancoVO)).thenReturn("LOJA 01");
        
        VRException assertThrows = assertThrows(VRException.class, () -> 
                                            service.verificaLojaMatriz(configuracaoBancoVO));
        
        assertEquals("Loja Origem LOJA 01 já selecionada como principal!", assertThrows.getMessage());
    }
    
    @Test
    public void testGetLojaMapeada() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        List<ConfiguracaoBancoLojaVO> lojas = new ArrayList<>();
        ConfiguracaoBancoLojaVO lojaVO = new ConfiguracaoBancoLojaVO();
        
        lojaVO.setId(1);
        lojaVO.setDescricaoVR("LOJA VR 01");
        lojas.add(lojaVO);
        
        lojaVO = new ConfiguracaoBancoLojaVO();
        lojaVO.setId(2);
        lojaVO.setDescricaoVR("LOJA VR 02");
        lojas.add(lojaVO);
        
        when(configuracaoDAO.getLojaMapeada(anyInt())).thenReturn(lojas);
        
        assertEquals(2, service.getLojaMapeada(anyInt()).size());
    }
    
    @Test
    public void testVerificaSituacaoLoja() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        configuracaoBancoLojaVO.setSituacaoMigracao(ESituacaoMigracao.VALIDACAO);
        
        VRException assertThrows = assertThrows(VRException.class, () -> 
                                            service.verificaSituacaoLoja(configuracaoBancoLojaVO));
        
        assertEquals("Processo de migração iniciado, não é possível excluir a loja mapeada!", assertThrows.getMessage());
    }
    
    @Test
    public void testExcluirLojaMapeada() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        configuracaoBancoLojaVO.setSituacaoMigracao(ESituacaoMigracao.CONFIGURANDO);
        
        service.excluirLojaMapeada(configuracaoBancoLojaVO);
        
        Mockito.verify(configuracaoDAO, Mockito.times(1)).excluirLojaMapeada(configuracaoBancoLojaVO);
    }
    
    @Test
    public void testSalvar() throws Exception {
        LojaDAO lojaDAO = Mockito.mock(LojaDAO.class);
        ConfiguracaoBaseDadosDAO configuracaoDAO = Mockito.mock(ConfiguracaoBaseDadosDAO.class);
        MapaLojaService service = new MapaLojaService(lojaDAO, configuracaoDAO);
        
        ConfiguracaoBaseDadosVO configuracaoBancoVO = new ConfiguracaoBaseDadosVO();
        ConfiguracaoBancoLojaVO configuracaoBancoLojaVO = new ConfiguracaoBancoLojaVO();
        
        configuracaoBancoLojaVO.setId(0);
        configuracaoBancoVO.setConfiguracaoBancoLoja(configuracaoBancoLojaVO);
        
        service.salvar(configuracaoBancoVO);
        
        Mockito.verify(configuracaoDAO, Mockito.times(1)).inserirLoja(configuracaoBancoVO);
    }
}
