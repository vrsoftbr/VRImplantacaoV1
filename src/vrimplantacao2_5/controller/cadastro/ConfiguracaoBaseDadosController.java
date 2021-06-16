package vrimplantacao2_5.controller.cadastro;

import java.util.List;
import vrimplantacao2_5.service.cadastro.ConfiguracaoBaseDadosService;
import vrimplantacao2_5.service.cadastro.panelobserver.PanelObserver;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosController {
    
    ConfiguracaoBaseDadosService service = new ConfiguracaoBaseDadosService();
    
    public List<SistemaVO> getSistema() {
        return service.getSistema();
    }
    
    public List<BancoDadosVO> getBancoDadosPorSistema(int idSistema) {
        return service.getBancoDadosPorSistema(idSistema);
    }
    
    public javax.swing.JPanel exibiPainelConexao(PanelObserver conexaoBD, int idSistema, int idBancoDados) {
        return service.exibiPainelConexao(conexaoBD, idSistema, idBancoDados);
    }
}
