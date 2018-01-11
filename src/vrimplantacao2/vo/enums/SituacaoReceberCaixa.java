package vrimplantacao2.vo.enums;

/**
 * Situação de uma trasação de recebimento do caixa.
 * @author Leandro
 */
public enum SituacaoReceberCaixa {
    
    ABERTO (0),
    FECHADO (1);
    
    private int id;

    private SituacaoReceberCaixa(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
