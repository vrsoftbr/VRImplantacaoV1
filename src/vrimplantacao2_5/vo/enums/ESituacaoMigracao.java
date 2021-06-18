package vrimplantacao2_5.vo.enums;

/**
 *
 * @author guilhermegomes
 */
public enum ESituacaoMigracao {
    
    CONFIGURANDO(1),
    IMPORTANDO(2),
    VALIDACAO(3),
    CONCLUIDO(4);
    
    private final int id;
    
    private ESituacaoMigracao(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public static ESituacaoMigracao getById(int id) {
        for (ESituacaoMigracao sm: values()) {
            if (sm.getId() == id) {
                return sm;
            }
        }
        return null;
    }
}
