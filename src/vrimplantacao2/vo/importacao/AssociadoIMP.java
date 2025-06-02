package vrimplantacao2.vo.importacao;

/**
 * Representa a importação do associado.
 *
 * @author Wesley
 */
public class AssociadoIMP {

    private String impIdAssociado;
    private String impIdProduto;
    private String descricaoAssociado;
    private int qtdEmbalagem;
    private String impIdAssociadoItem;
    private String impIdProdutoItem;
    private String descricaoAssociadoItem;
    private int qtdEmbalagemItem = 1;
    private double percentualPreco = 0;
    private boolean aplicaPreco = false;
    private boolean aplicaCusto = false;
    private boolean aplicaEstoque = true;
    private double  percentualcustoestoque = 0;

    public String getImpIdAssociado() {
        return impIdAssociado;
    }

    public void setImpIdAssociado(String impIdAssociado) {
        this.impIdAssociado = impIdAssociado;
    }

    public String getImpIdProduto() {
        return impIdProduto;
    }

    public void setImpIdProduto(String impIdProduto) {
        this.impIdProduto = impIdProduto;
    }

    public String getDescricaoAssociado() {
        return descricaoAssociado;
    }

    public void setDescricaoAssociado(String descricaoAssociado) {
        this.descricaoAssociado = descricaoAssociado;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public String getImpIdAssociadoItem() {
        return impIdAssociadoItem;
    }

    public void setImpIdAssociadoItem(String impIdAssociadoItem) {
        this.impIdAssociadoItem = impIdAssociadoItem;
    }

    public String getImpIdProdutoItem() {
        return impIdProdutoItem;
    }

    public void setImpIdProdutoItem(String impIdProdutoItem) {
        this.impIdProdutoItem = impIdProdutoItem;
    }

    public String getDescricaoAssociadoItem() {
        return descricaoAssociadoItem;
    }

    public void setDescricaoAssociadoItem(String descricaoAssociadoItem) {
        this.descricaoAssociadoItem = descricaoAssociadoItem;
    }

    public int getQtdEmbalagemItem() {
        return qtdEmbalagemItem;
    }

    public void setQtdEmbalagemItem(int qtdEmbalagemItem) {
        this.qtdEmbalagemItem = qtdEmbalagemItem;
    }

    public double getPercentualPreco() {
        return percentualPreco;
    }

    public void setPercentualPreco(double percentualPreco) {
        this.percentualPreco = percentualPreco;
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

    public double getPercentualcustoestoque() {
        return percentualcustoestoque;
    }

    public void setPercentualcustoestoque(double percentualcustoestoque) {
        this.percentualcustoestoque = percentualcustoestoque;
    }
}
