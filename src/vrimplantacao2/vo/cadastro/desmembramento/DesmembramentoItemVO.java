package vrimplantacao2.vo.cadastro.desmembramento;

import vrimplantacao2.utils.MathUtils;

public class DesmembramentoItemVO {
    
    private int id;
    private int idDesmembramento;
    private int idProduto;
    private double percentualEstoque;
    private double percentualPerda = 0.0d;
    private double percentualDesossa = 0.0d;
    private double percentualCusto = 0.0d;

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

    public double getPercentualEstoque() {
        return percentualEstoque;
    }

    public void setPercentualEstoque(double percentualEstoque) {
        this.percentualEstoque = MathUtils.round(percentualEstoque, 4);
    }

    public double getPercentualPerda() {
        return percentualPerda;
    }

    public void setPercentualPerda(double percentualPerda) {
        this.percentualPerda = percentualPerda;
    }

    public double getPercentualDesossa() {
        return percentualDesossa;
    }

    public void setPercentualDesossa(double percentualDesossa) {
        this.percentualDesossa = percentualDesossa;
    }

    public double getPercentualCusto() {
        return percentualCusto;
    }

    public void setPercentualCusto(double percentualCusto) {
        this.percentualCusto = percentualCusto;
    }
    
}
