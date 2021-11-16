package vrimplantacao2_5.observer.publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrimplantacao2_5.observer.listeners.EventListener;

/**
 *
 * @author Desenvolvimento
 */
public class EventManager {

    Map<String, List<EventListener>> listeners = new HashMap<>();
    
    public EventManager(String...operartions) {
        for (String operation : operartions) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }
    
    public void subscribe(String eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        users.add(listener);
    }
    
    public void unsubscribe(String eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        users.remove(listener);
    }
    
    public void notify(String eventType, VRInternalFrame frame) {
        List<EventListener> users = listeners.get(eventType);
        for (EventListener listener : users) {
            listener.update(eventType, frame);
        }
    }
}
