package vrimplantacao2_5.controller.cadastro.configuracao;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.configuracao.ConsultaConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.service.cadastro.configuracao.ConsultaConfiguracaoBancoDadosService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaConfiguracaoBancoDadosController {
    
    private ConsultaConfiguracaoBaseDadosGUI configuracaoGUI = null;
    private final ConsultaConfiguracaoBancoDadosService service = new ConsultaConfiguracaoBancoDadosService();
    private List<ConfiguracaoBancoVO> conexoes = null;

    public ConsultaConfiguracaoBancoDadosController() {}
    
    public ConsultaConfiguracaoBancoDadosController(ConsultaConfiguracaoBaseDadosGUI configuracaoGUI) {
        this.configuracaoGUI = configuracaoGUI;
    }
    
    public void consultar() throws Exception {
        this.conexoes = service.consultar();
        configuracaoGUI.consultar();
    }
    
    public List<ConfiguracaoBancoVO> getConexao() {
        return conexoes;
    }
}
