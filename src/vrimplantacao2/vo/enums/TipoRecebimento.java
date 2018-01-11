package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum TipoRecebimento {
    DEPOSITO_BANCARIO (0),
    CARTEIRA (1),
    DINHEIRO (2),
    CHEQUE (3),
    PRODUTO (4),
    BOLETO (5),
    CARTAO_CREDITO (6),
    CARTAO_DEBITO (7),
    CARTEIRA_2 (8),
    S_BOLETO (9),
    DOC_TED (10);
    
    private int id;

    public int getId() {
        return id;
    }
    
    private TipoRecebimento(int id) {
        this.id = id;
    }

}
