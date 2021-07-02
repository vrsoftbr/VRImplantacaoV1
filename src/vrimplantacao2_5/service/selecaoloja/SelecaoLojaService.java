package vrimplantacao2_5.service.selecaoloja;

import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class SelecaoLojaService {
    
    private final ConfiguracaoBaseDadosDAO configuracaoDAO;

    public SelecaoLojaService(ConfiguracaoBaseDadosDAO configuracaoDAO) {
        this.configuracaoDAO = configuracaoDAO;
    }
    
    public SelecaoLojaService() {
        this.configuracaoDAO = new ConfiguracaoBaseDadosDAO();
    }
    
    public List<ConfiguracaoBaseDadosVO> consultar() {
        List<ConfiguracaoBaseDadosVO> result = null;
        
        try {
            result = configuracaoDAO.consultar();
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Mapa de Loja");
        }
        
        return result;
    }
    
    public List<ConfiguracaoBancoLojaVO> getLojaMapeada(int idConexao) {
        List<ConfiguracaoBancoLojaVO> lojas = null;

        try {
            lojas = configuracaoDAO.getLojaMapeada(idConexao);
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Mapa de Loja");
        }

        return lojas;
    }
}
