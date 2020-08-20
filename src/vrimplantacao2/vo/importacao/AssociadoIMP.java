package vrimplantacao2.vo.importacao;

/**
 * Representa a importação do associado.
 * @author Leandro
 */
public class AssociadoIMP {
    
    private String id;
    private String descricao;
    private int qtdEmbalagem;
    private int qtdEmbalagemItem = 1;
    private String produtoAssociadoId;
    private String descricaoProdutoAssociado;
    private double percentualPreco = 0;
    private double percentualCusto = 0;
    private boolean aplicaPreco = false;
    private boolean aplicaCusto = false;
    private boolean aplicaEstoque = true;

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

    /**
     * @return the qtdEmbalagemItem
     */
    public int getQtdEmbalagemItem() {
        return qtdEmbalagemItem;
    }

    /**
     * @param qtdEmbalagemItem the qtdEmbalagemItem to set
     */
    public void setQtdEmbalagemItem(int qtdEmbalagemItem) {
        this.qtdEmbalagemItem = qtdEmbalagemItem;
    }

    public boolean isAplicaPreco() {
        return aplicaPreco;
    }

    public void setAplicaPreco(boolean aplicaPreco) {
        this.aplicaPreco = aplicaPreco;
    }

    public boolean isAplicaCusto() {
        return aplicaCusto;
    }

    public void setAplicaCusto(boolean aplicaCusto) {
        this.aplicaCusto = aplicaCusto;
    }

    public boolean isAplicaEstoque() {
        return aplicaEstoque;
    }

    public void setAplicaEstoque(boolean aplicaEstoque) {
        this.aplicaEstoque = aplicaEstoque;
    }
    
}
