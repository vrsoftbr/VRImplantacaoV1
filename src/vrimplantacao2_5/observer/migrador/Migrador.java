package vrimplantacao2_5.observer.migrador;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrimplantacao2_5.observer.publisher.EventManager;

/**
 *
 * @author Desenvolvimento
 */
public class Migrador {

    public EventManager events;
    public VRInternalFrame frame;
    public VRMdiFrame i_mdiFrame;
    
    public Migrador() {
        this.events = new EventManager("abrir", "migracao");
    }
    
    public void abrir() throws Exception {
        this.frame = new VRInternalFrame(i_mdiFrame);
        events.notify("abrir", frame);
    }
    
    public void migrar() throws Exception {
        if (this.frame != null) {
            events.notify("migracao", frame);
        } else {
            throw new Exception("Não abriu a tela de migração");
        }
    }
}
