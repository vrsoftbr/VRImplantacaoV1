package vrimplantacao2_5.controller.cadastro.configuracao;

import java.util.List;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.service.cadastro.configuracao.MapaLojaService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaController {
    
    private MapaLojaService mapaLojaService = null;
    private ConfiguracaoBaseDadosGUI configuracaoBaseDadosGUI = null;
    private List<ConfiguracaoBancoLojaVO> lojasMapeadas = null;
    
    public MapaLojaController() throws Exception {}

    public MapaLojaController(ConfiguracaoBaseDadosGUI configuracaoBaseDadosGUI) {
        this.mapaLojaService = new MapaLojaService();
        this.configuracaoBaseDadosGUI = configuracaoBaseDadosGUI;
    }
    
    public List<LojaVO> getLojaVR() {
        return mapaLojaService.getLojaVR();
    }
    
    public void salvar(ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {
        mapaLojaService.salvar(configuracaoBancoVO);
    }
    
    public void consultaLojaMapeada(int idConexao) throws Exception {
        lojasMapeadas = mapaLojaService.getLojaMapeada(idConexao);
        
        configuracaoBaseDadosGUI.consultaConfiguracaoLoja();
    }

    public void atualizarConsultarLojaMapeada(int idConexao) throws Exception {
        lojasMapeadas = mapaLojaService.getLojaMapeada(idConexao);
        
        configuracaoBaseDadosGUI.atualizarConsultaConfiguracaoLoja(idConexao);
    }
    
    public List<ConfiguracaoBancoLojaVO> getLojaMapeada() {
        return lojasMapeadas;
    }
    
    public void excluirLojaMapeada(ConfiguracaoBancoLojaVO configuracaoBancoLojaVO) throws Exception {
        mapaLojaService.excluirLojaMapeada(configuracaoBancoLojaVO);
    }
    
    public void alterarSituacaoMigracao(String idLojaOrigem, int idLojaVR, int situacaoMigracao, int idConexao) throws Exception {
        mapaLojaService.alterarSituacaoMigracao(idLojaOrigem, idLojaVR, situacaoMigracao);
        this.atualizarConsultarLojaMapeada(idConexao);
    }
}
