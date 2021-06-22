package vrimplantacao2_5.controller.cadastro.configuracao;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.configuracao.ConsultaConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.service.cadastro.configuracao.ConsultaConfiguracaoBaseDadosService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaConfiguracaoBaseDadosController {
    
    private ConsultaConfiguracaoBaseDadosGUI configuracaoGUI = null;
    private final ConsultaConfiguracaoBaseDadosService service = new ConsultaConfiguracaoBaseDadosService();
    private List<ConfiguracaoBaseDadosVO> conexoes = null;

    public ConsultaConfiguracaoBaseDadosController() {}
    
    public ConsultaConfiguracaoBaseDadosController(ConsultaConfiguracaoBaseDadosGUI configuracaoGUI) {
        this.configuracaoGUI = configuracaoGUI;
    }
    
    public void consultar() throws Exception {
        this.conexoes = service.consultar();
        configuracaoGUI.consultar();
    }
    
    public List<ConfiguracaoBaseDadosVO> getConexao() {
        return conexoes;
    }
}
