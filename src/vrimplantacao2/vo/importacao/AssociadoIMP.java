package vrimplantacao2.vo.importacao;

/**
 * Representa a importação do associado.
 * @author Leandro
 */
public class AssociadoIMP {
    
    private String id;
    private String descricao;
    private int qtdEmbalagem;
    private String produtoAssociadoId;
    private String descricaoProdutoAssociado;
    private double percentualPreco = 0;
    private double percentualCusto = 0;

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

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public String getProdutoAssociadoId() {
        return produtoAssociadoId;
    }

    public void setProdutoAssociadoId(String produtoAssociadoId) {
        this.produtoAssociadoId = produtoAssociadoId;
    }

    public String getDescricaoProdutoAssociado() {
        return descricaoProdutoAssociado;
    }

    public void setDescricaoProdutoAssociado(String descricaoProdutoAssociado) {
        this.descricaoProdutoAssociado = descricaoProdutoAssociado;
    }

    public double getPercentualPreco() {
        return percentualPreco;
    }

    public void setPercentualPreco(double percentualPreco) {
        this.percentualPreco = percentualPreco;
    }

    public double getPercentualCusto() {
        return percentualCusto;
    }

    public void setPercentualCusto(double percentualCusto) {
        this.percentualCusto = percentualCusto;
    }
    
}
