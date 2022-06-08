package vrimplantacao2.vo.cadastro.desmembramento;

/**
 * Classe que representa a tabela desmembramentoitem.
 * @author Alan
 */
public class DesmembramentoItemVO {
    
    private int id;
    private int idDesmembramento;
    private int idProduto;
    private int percentualEstoque;
    private int percentualPerda;
    private int percentualDesossa;
    private int percentualCusto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDesmembramento() {
        return idDesmembramento;
    }

    public void setIdDesmembramento(int idDesmembramento) {
        this.idDesmembramento = idDesmembramento;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getPercentualEstoque() {
        return percentualEstoque;
    }

    public void setPercentualEstoque(int percentualEstoque) {
        this.percentualEstoque = percentualEstoque;
    }

    public int getPercentualPerda() {
        return percentualPerda;
    }

    public void setPercentualPerda(int percentualPerda) {
        this.percentualPerda = percentualPerda;
    }

    public int getPercentualDesossa() {
        return percentualDesossa;
    }

    public void setPercentualDesossa(int percentualDesossa) {
        this.percentualDesossa = percentualDesossa;
    }

    public int getPercentualCusto() {
        return percentualCusto;
    }

    public void setPercentualCusto(int percentualCusto) {
        this.percentualCusto = percentualCusto;
    }
    
}
