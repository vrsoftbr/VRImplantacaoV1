package vrimplantacao2_5.service.selecaoloja;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrimplantacao2_5.gui.migracao2_5.Migracao2_5GUI;
import vrimplantacao2_5.gui.sistema.Hipcom2_5GUI;
import vrimplantacao2_5.gui.sistema.SysPdv2_5GUI;
import vrimplantacao2_5.gui.sistema.Uniplus2_5GUI;
import vrimplantacao2_5.gui.sistema.VRToVR2_5GUI;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author guilhermegomes
 */
public abstract class InternalFrameFactory {

    /**
     * Build da interface GUI do sistema selecionado
     * @param sistema Sistema informado na conexão
     * @param frame MenuGUI
     * @return VRInternalFrame desejado
     * @throws Exception 
     */
    public static VRInternalFrame getInternalFrame(ESistema sistema, VRMdiFrame frame) throws Exception {
        VRInternalFrame internalFrame;

        switch (sistema) {
            case SYSPDV:
                internalFrame = new SysPdv2_5GUI(frame);
                break;
            case UNIPLUS:
                internalFrame = new Uniplus2_5GUI(frame);
                break;
            case VRMASTER:
                internalFrame = new VRToVR2_5GUI(frame);
                break;
            case HIPCOM:
                internalFrame = new Hipcom2_5GUI(frame);
                break;
            default:
                internalFrame = new Migracao2_5GUI(frame, sistema);                
        }

        return internalFrame;
    }
}
