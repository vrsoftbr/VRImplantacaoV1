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

    public AssociadoItemIMP(String descricao, String idProduto, int qtdEmbalagem) {
        this.descricao = descricao;
        this.idProduto = idProduto;
        this.qtdEmbalagem = qtdEmbalagem;
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
    
}
