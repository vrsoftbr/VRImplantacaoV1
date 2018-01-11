package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum TipoVistaPrazo {
    A_VISTA (0),
    PRAZO (1);
    
    private final int id;
    private TipoVistaPrazo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
        
}
