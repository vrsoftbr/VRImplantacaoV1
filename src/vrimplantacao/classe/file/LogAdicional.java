package vrimplantacao.classe.file;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Leandro.Caires
 */
public class LogAdicional {
    private final Map<String, Object> fields = new LinkedHashMap<>();

    public Object put(String key, Object value) {
        return fields.put(key, value);
    }

    public int size() {
        return fields.size();
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }

    public Object remove(String key) {
        return fields.remove(key);
    }

    public void clear() {
        fields.clear();
    }

    public Object get(Object key) {
        return fields.get(key);
    }

    public Set<String> keySet() {
        return fields.keySet();
    }
    
}
