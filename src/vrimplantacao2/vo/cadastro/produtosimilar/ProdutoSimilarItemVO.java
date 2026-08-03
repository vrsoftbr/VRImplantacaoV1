package vrimplantacao2.vo.cadastro.produtosimilar;

/**
 * Classe que representa a tabela produto similar item.
 * @author Wesley
 */
public class ProdutoSimilarItemVO {
    
    private int id;
    private int idProduto;
    private ProdutoSimilarVO idProdutoSimilar;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public ProdutoSimilarVO getIdProdutoSimilar() {
        return idProdutoSimilar;
    }

    public void setIdProdutoSimilar(ProdutoSimilarVO idProdutoSimilar) {
        this.idProdutoSimilar = idProdutoSimilar;
    }
}
