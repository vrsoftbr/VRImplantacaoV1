package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum TipoIva {
    PERCENTUAL (0),
    VALOR (1);
    
    private int id;

    private TipoIva(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
