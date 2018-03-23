package vrimplantacao2.vo.cadastro.comprador;

/**
 *
 * @author Leandro
 */
public class CompradorAnteriorVO {
    
    private String sistema;
    private String loja;
    private String id;
    private int codigoAtual;
    private String descricao;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
