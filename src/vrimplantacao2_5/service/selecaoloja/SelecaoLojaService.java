package vrimplantacao2_5.service.selecaoloja;

import java.util.List;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.enums.ESistema;

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
    
    public List<ConfiguracaoBaseDadosVO> consultar(int idSistema) {
        List<ConfiguracaoBaseDadosVO> result = null;
        
        try {
            result = configuracaoDAO.consultar(idSistema);
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
    
    public VRInternalFrame construirInternalFrame(ESistema sistema, VRMdiFrame frame) throws Exception {
        return InternalFrameFactory.getInternalFrame(sistema, frame);
    }
}
