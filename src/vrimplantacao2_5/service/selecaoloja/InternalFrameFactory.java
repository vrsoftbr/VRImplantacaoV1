package vrimplantacao2_5.service.selecaoloja;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrimplantacao2_5.gui.cadastro.configuracao.ConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.gui.sistema.Avistare2_5GUI;
import vrimplantacao2_5.gui.sistema.GatewaySistemas2_5GUI;
import vrimplantacao2_5.gui.sistema.Hipcom2_5GUI;
import vrimplantacao2_5.gui.sistema.SG2_5GUI;
import vrimplantacao2_5.gui.sistema.Sygma2_5GUI;
import vrimplantacao2_5.gui.sistema.BomSoft2_5GUI;
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
            case GATEWAYSISTEMAS:
                internalFrame = new GatewaySistemas2_5GUI(frame);
                break;
            case SG:
                internalFrame = new SG2_5GUI(frame);
                break;
            case SYGMA:
                internalFrame = new Sygma2_5GUI(frame);
            case AVISTARE:
                internalFrame = new Avistare2_5GUI(frame);
                break;
            case BOMSOFT:
                internalFrame = new BomSoft2_5GUI(frame);
                break;
            default:
                internalFrame = null;
        }

        return internalFrame;
    }
    
    /**
     * Build da interface GUI do sistema selecionado
     * @param sistema Sistema informado na conexão
     * @param frame MenuGUI
     * @param baseDadosGui ConfiguracaoBaseDadoGUI
     * @return VRInternalFrame desejado
     * @throws Exception 
     */
    
    public static VRInternalFrame getInternalFrame(ESistema sistema, VRMdiFrame frame, ConfiguracaoBaseDadosGUI baseDadosGui) throws Exception {
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
            case GATEWAYSISTEMAS:
                internalFrame = new GatewaySistemas2_5GUI(frame);
                break;
            case SG:
                internalFrame = new SG2_5GUI(frame);
                break;
            case SYGMA:
                internalFrame = new Sygma2_5GUI(frame);
            case AVISTARE:
                internalFrame = new Avistare2_5GUI(frame);
                break;
            case BOMSOFT:
                internalFrame = new BomSoft2_5GUI(frame);
                break;
            default:
                internalFrame = null;
        }

        return internalFrame;
    }
}