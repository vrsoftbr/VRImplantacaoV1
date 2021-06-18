package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.List;
import org.openide.util.Exceptions;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoSistemaDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaConfiguracaoBancoDadosService {
    
    ConfiguracaoSistemaDAO configuracaoDAO = null;

    public ConsultaConfiguracaoBancoDadosService() {
        configuracaoDAO = new ConfiguracaoSistemaDAO();
    }
    
    public ConsultaConfiguracaoBancoDadosService(ConfiguracaoSistemaDAO configuracaoDAO) {
        this.configuracaoDAO = configuracaoDAO;
    }
    
    public List<ConfiguracaoBancoVO> consultar() {
        List<ConfiguracaoBancoVO> result = null;
        
        try {
            result = configuracaoDAO.consultar();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return result;
    }
}
