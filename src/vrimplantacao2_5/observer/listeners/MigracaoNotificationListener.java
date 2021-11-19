package vrimplantacao2_5.observer.listeners;

import vrframework.bean.internalFrame.VRInternalFrame;

/**
 *
 * @author Desenvolvimento
 */
public class MigracaoNotificationListener implements EventListener {

    private boolean migrou;
    
    public MigracaoNotificationListener(boolean migrou) {
        this.migrou = migrou;
    }

    @Override
    public void update(String eventType, VRInternalFrame frame) {
        System.out.println("Migrou");
    }
}
