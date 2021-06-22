package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.List;
import org.openide.util.Exceptions;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class ConsultaConfiguracaoBaseDadosService {
    
    ConfiguracaoBaseDadosDAO configuracaoDAO = null;

    public ConsultaConfiguracaoBaseDadosService() {
        configuracaoDAO = new ConfiguracaoBaseDadosDAO();
    }
    
    public ConsultaConfiguracaoBaseDadosService(ConfiguracaoBaseDadosDAO configuracaoDAO) {
        this.configuracaoDAO = configuracaoDAO;
    }
    
    public List<ConfiguracaoBaseDadosVO> consultar() {
        List<ConfiguracaoBaseDadosVO> result = null;
        
        try {
            result = configuracaoDAO.consultar();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return result;
    }
}
