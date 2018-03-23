package vrimplantacao2.vo.importacao;

/**
 * Comprador de produtos.
 * @author Leandro
 */
public class CompradorIMP {
    
    private String id;
    private String descricao;

    public CompradorIMP() {
    }

    public CompradorIMP(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
