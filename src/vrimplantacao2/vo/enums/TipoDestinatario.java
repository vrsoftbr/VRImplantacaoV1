package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum TipoDestinatario {
    
    FORNECEDOR(0), CLIENTE_EVENTUAL(1);

    public static TipoDestinatario get(int tipo) {
        for (TipoDestinatario td : values()) {
            if (td.getId() == tipo) {
                return td;
            }
        }
        return null;
    }
    
    private int id;

    private TipoDestinatario(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
