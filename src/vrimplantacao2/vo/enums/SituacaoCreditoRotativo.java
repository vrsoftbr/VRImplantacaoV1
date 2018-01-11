package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum SituacaoCreditoRotativo {
    ABERTO (0),
    BAIXADO (1),
    CANCELADO (2);
    
    private int id;

    private SituacaoCreditoRotativo(int id) {
        this.id = id;
    }
    
    public int getID() {
        return id;
    }
}
