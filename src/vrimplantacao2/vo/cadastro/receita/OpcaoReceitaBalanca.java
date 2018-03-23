package vrimplantacao2.vo.cadastro.receita;

import java.util.HashMap;
import java.util.Map;

/**
 * Opções de importação de receita balança.
 * @author Leandro
 */
public enum OpcaoReceitaBalanca {
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
