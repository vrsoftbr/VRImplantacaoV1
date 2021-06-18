package vrimplantacao2_5.controller.cadastro.configuracao;

import java.util.List;
import vrimplantacao2_5.service.cadastro.configuracao.ConsultaConfiguracaoBancoDadosService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaConfiguracaoBancoDadosController {
    
    private final ConsultaConfiguracaoBancoDadosService service = new ConsultaConfiguracaoBancoDadosService();
    
    public List<ConfiguracaoBancoVO> consultar() {
        return service.consultar();
    }
}
