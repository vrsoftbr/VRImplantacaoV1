package vrimplantacao2.vo.importacao;

/**
 * Representa a importação do associado.
 *
 * @author Wesley
 */
public class ProdutoSimilarIMP {

    private String idProdutoSimilar;
    private String descricaoTitulo;
    private boolean ativo;
    private String idProdutoSimilarItem;
    private String idProduto;
    private String descricaoItem;

    public String getIdProdutoSimilar() {
        return idProdutoSimilar;
    }

    public void setIdProdutoSimilar(String idProdutoSimilar) {
        this.idProdutoSimilar = idProdutoSimilar;
    }

    public String getDescricaoTitulo() {
        return descricaoTitulo;
    }

    public void setDescricaoTitulo(String descricaoTitulo) {
        this.descricaoTitulo = descricaoTitulo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getIdProdutoSimilarItem() {
        return idProdutoSimilarItem;
    }

    public void setIdProdutoSimilarItem(String idProdutoSimilarItem) {
        this.idProdutoSimilarItem = idProdutoSimilarItem;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }
    
    public String getDescricaoItem() {
        return descricaoItem;
    }

    public void setDescricaoItem(String descricaoItem) {
        this.descricaoItem = descricaoItem;
    }
}
