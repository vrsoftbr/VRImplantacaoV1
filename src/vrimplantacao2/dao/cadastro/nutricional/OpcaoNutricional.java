package vrimplantacao2.dao.cadastro.nutricional;

import java.util.HashMap;
import java.util.Map;

/**
 * Opções de importação de nutricional.
 * @author Leandro
 */
public enum OpcaoNutricional {
    FILIZOLA,
    TOLEDO;
    
    /**
     * Parâmetros de importação.
     */
    private Map<String, Object> parameters = new HashMap<>();

    /**
     * Lista de parâmetros da importação.
     * @return 
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
}
