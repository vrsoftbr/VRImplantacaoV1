package vrimplantacao2.vo.cadastro.financeiro;

/**
 *
 * @author Leandro
 */
public enum SituacaoPagarFornecedorParcela {
    ABERTO(0), PAGO(1);
    private int id;

    private SituacaoPagarFornecedorParcela(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
