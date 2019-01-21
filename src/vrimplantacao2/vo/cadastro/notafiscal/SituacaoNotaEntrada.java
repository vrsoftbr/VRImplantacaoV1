package vrimplantacao2.vo.cadastro.notafiscal;

/**
 *
 * @author Leandro
 */
public enum SituacaoNotaEntrada {
    NAO_FINALIZADO (0),
    FINALIZADO (1);
    
    private int id;

    private SituacaoNotaEntrada(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
