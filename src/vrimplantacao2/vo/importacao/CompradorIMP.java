package vrimplantacao2.vo.importacao;

/**
 * Comprador de produtos.
 * @author Leandro
 */
public class CompradorIMP {

    /**
     * @return the manterId
     */
    public Boolean getManterId() {
        return manterId;
    }

    /**
     * @param manterId the manterId to set
     */
    public void setManterId(Boolean manterId) {
        this.manterId = manterId;
    }
    
    private String id;
    private String descricao;
    private Boolean manterId = false;

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
