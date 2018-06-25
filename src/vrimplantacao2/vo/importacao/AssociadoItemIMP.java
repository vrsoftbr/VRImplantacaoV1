package vrimplantacao2.vo.importacao;

/**
 *
 * @author Leandro
 */
public class AssociadoItemIMP {
    
    private String idProduto;
    private String descricao;
    private int qtdEmbalagem;
    private double percentualPreco = 0;
    private double percentualCusto = 0;
    private boolean aplicaPreco;
    private boolean aplicaCusto;
    private boolean aplicaEstoque;

    public AssociadoItemIMP(String descricao, String idProduto, int qtdEmbalagem, boolean aplicaPreco, boolean aplicaCusto, boolean aplicaEstoque) {
        this.descricao = descricao;
        this.idProduto = idProduto;
        this.qtdEmbalagem = qtdEmbalagem;
        this.aplicaPreco = aplicaPreco;
        this.aplicaCusto = aplicaCusto;
        this.aplicaEstoque = aplicaEstoque;
    }

    public AssociadoItemIMP() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
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
