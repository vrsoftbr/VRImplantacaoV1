package vrimplantacao2.vo.cadastro.convenio.transacao;

/**
 *
 * @author Leandro
 */
public enum SituacaoTransacaoConveniado {
    OK (0),
    PENDENTE (1),
    CANCELADO (2),
    DESFEITO (3);
    
    private int id;
    
    private SituacaoTransacaoConveniado(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
