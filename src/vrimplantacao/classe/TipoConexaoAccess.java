package vrimplantacao.classe;

/**
 *
 * @author Leandro
 */
public enum TipoConexaoAccess {
    
    DRIVER (0),
    FONTE_DE_DADOS (1);
    
    public static TipoConexaoAccess get(int id) {
        for (TipoConexaoAccess tca: values()) {
            if (tca.getId() == id) {
                return tca;
            }
        }
        return  null;
    }
    
    private int id;

    private TipoConexaoAccess(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
