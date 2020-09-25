package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum TipoVistaPrazo {
    A_VISTA (0),
    PRAZO (1);

    public static TipoVistaPrazo getById(int id) {
        for (TipoVistaPrazo tp: values()) {
            if (tp.getId() == id) {
                return tp;
            }
        }
        return null;
    }
    
    private final int id;
    private TipoVistaPrazo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
        
}
