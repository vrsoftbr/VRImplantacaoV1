package vrimplantacao2_5.service.selecaoloja;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author guilhermegomes
 */
public abstract class InternalFrameFactory {
    
    /**
     * Build da interface GUI do sistema selecionado
     *
     * @param sistema informado na conexão
     * @param frame MenuGUI
     * @return VRInternalFrame desejado
     * @throws Exception
     */
    public static VRInternalFrame getInternalFrame(ESistema sistema, VRMdiFrame frame) throws Exception {
        return sistema.getInternalFrame(frame);
    }
}
