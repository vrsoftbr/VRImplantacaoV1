package vrimplantacao2_5.controller.selecaoloja;

import java.util.List;
import vrimplantacao2_5.service.selecaoloja.SelecaoLojaService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class SelecaoLojaController {
    
    SelecaoLojaService service = null;

    public SelecaoLojaController() {
        service = new SelecaoLojaService();
    }
    
    public List<ConfiguracaoBaseDadosVO> consultar() throws Exception {
        return service.consultar();
    }
    
    public List<ConfiguracaoBancoLojaVO> getLojaMapeada(int idConexao) {
        return service.getLojaMapeada(idConexao);
    }
}
