package vrimplantacao2.vo.cadastro.oferta;

public enum SituacaoOferta {
    CANCELADO (0, "CANCELADO"),
    ATIVO (1, "ATIVO");
    
    private int id;
    private String descricao;

    private SituacaoOferta(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }    
    
    public static SituacaoOferta getById(int id) {
        for (SituacaoOferta sit: values()) {
            if (sit.getId() == id) {
                return sit;
            }
        }
        return ATIVO;
    }
    
}
