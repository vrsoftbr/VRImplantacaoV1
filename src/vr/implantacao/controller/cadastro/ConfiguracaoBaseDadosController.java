package vr.implantacao.controller.cadastro;

import java.util.List;
import vr.implantacao.service.cadastro.ConfiguracaoBaseDadosService;
import vr.implantacao.vo.cadastro.BancoDadosVO;
import vr.implantacao.vo.cadastro.SistemaVO;

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
    
    public javax.swing.JPanel exibiPainelConexao(int idSistema, int idBancoDados) {
        return service.exibiPainelConexao(idSistema, idBancoDados);
    }
}
