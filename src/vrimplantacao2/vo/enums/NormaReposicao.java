package vrimplantacao2.vo.enums;

public enum NormaReposicao {
    
    CAIXA (1, "CAIXA"),
    CAMADA (2, "CAMADA"),
    PALETE (3, "PALETE");
    
    private final int id;
    private final String descricao;

    private NormaReposicao(int id, String descricao) {
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
