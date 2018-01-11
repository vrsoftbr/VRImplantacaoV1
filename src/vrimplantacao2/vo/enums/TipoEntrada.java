package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum TipoEntrada {
    
    OUTRAS (210, "Outras"), 
    COMPRA_MERCADORIAS (0, "Compra Mercadorias");
    
    private int id;
    private String descricao;

    private TipoEntrada(int id, String descricao) {
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
