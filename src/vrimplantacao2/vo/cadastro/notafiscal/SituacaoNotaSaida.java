package vrimplantacao2.vo.cadastro.notafiscal;

/**
 *
 * @author Leandro
 */
public enum SituacaoNotaSaida {
    NAO_FINALIZADO (0),
    FINALIZADO (1);
    
    private int id;

    private SituacaoNotaSaida(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
