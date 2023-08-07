package vrimplantacao2_5.controller.selecaoloja;

import java.util.List;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.service.selecaoloja.SelecaoLojaService;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.enums.ESistema;

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
    
    public List<ConfiguracaoBaseDadosVO> consultar(int idSistema) throws Exception {
        return service.consultar(idSistema);
    }
    
    public ConfiguracaoBaseDadosVO getConexao(int idConexao) throws Exception {
        return service.getConexao(idConexao);
    }
    
    public List<ConfiguracaoBancoLojaVO> getLojaMapeada(int idConexao) {
        return service.getLojaMapeada(idConexao);
    }
    
    public VRInternalFrame construirInternalFrame(ESistema sistema, VRMdiFrame frame) throws Exception {
        return service.construirInternalFrame(sistema, frame);
    }
    
    public VRInternalFrame construirInternalFrame(ESistema sistema, VRMdiFrame frame, ConfiguracaoBaseDadosGUI baseDadosGui) throws Exception {
        return service.construirInternalFrame(sistema, frame, baseDadosGui);
    }
}
