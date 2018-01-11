package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum SituacaoPagarOutrasDespesas {
    NAO_FINALIZADO (0, "Não Finalizado"),
    FINALIZADO (1, "Finalizado");
    
    private int id;
    private String descricao;

    private SituacaoPagarOutrasDespesas(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
    
    
}
