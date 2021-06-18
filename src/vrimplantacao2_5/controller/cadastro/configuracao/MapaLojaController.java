package vrimplantacao2_5.controller.cadastro.configuracao;

import java.util.List;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.service.cadastro.configuracao.MapaLojaService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaController {
    
    private final MapaLojaService mapaLojaService;
    private ConfiguracaoBaseDadosGUI configuracaoBaseDadosGUI = null;
    private List<ConfiguracaoBancoLojaVO> lojasMapeadas = null;

    public MapaLojaController(ConfiguracaoBaseDadosGUI configuracaoBaseDadosGUI) {
        this.mapaLojaService = new MapaLojaService();
        this.configuracaoBaseDadosGUI = configuracaoBaseDadosGUI;
    }
    
    public List<LojaVO> getLojaVR() {
        return mapaLojaService.getLojaVR();
    }
    
    public void salvar(ConfiguracaoBancoVO configuracaoBancoVO) {
        mapaLojaService.salvar(configuracaoBancoVO);
    }
    
    public void consultaLojaMapeada(int idConexao) throws Exception {
        lojasMapeadas = mapaLojaService.getLojaMapeada(idConexao);
        
        configuracaoBaseDadosGUI.consultaConfiguracaoLoja();
    }
    
    public List<ConfiguracaoBancoLojaVO> getLojaMapeada() {
        return lojasMapeadas;
    }
    
    public void excluirLojaMapeada(ConfiguracaoBancoLojaVO configuracaoBancoLojaVO, String title) {
        mapaLojaService.excluirLojaMapeada(configuracaoBancoLojaVO, title);
    }
}
