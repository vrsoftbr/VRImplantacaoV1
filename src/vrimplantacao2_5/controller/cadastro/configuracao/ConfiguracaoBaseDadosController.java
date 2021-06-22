package vrimplantacao2_5.controller.cadastro.configuracao;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.service.cadastro.configuracao.ConfiguracaoPanel;
import vrimplantacao2_5.service.cadastro.configuracao.ConfiguracaoBaseDadosService;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosController {
    
    ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService();
    ConfiguracaoBaseDadosGUI configuracaoGUI = null;

    public ConfiguracaoBaseDadosController() {}
    
    public ConfiguracaoBaseDadosController(ConfiguracaoBaseDadosGUI configuracaoGUI) {
        this.configuracaoGUI = configuracaoGUI;
    }
    
    public List<SistemaVO> getSistema() {
        return service.getSistema();
    }
    
    public List<BancoDadosVO> getBancoDadosPorSistema(int idSistema) {
        return service.getBancoDadosPorSistema(idSistema);
    }
    
    public ConfiguracaoPanel exibiPainelConexao(int idSistema, int idBancoDados) {
        return service.exibiPainelConexao(idSistema, idBancoDados);
    }
    
    public void salvar(ConfiguracaoBaseDadosVO conexaoVO) {
        service.salvar(conexaoVO);
    }
}
