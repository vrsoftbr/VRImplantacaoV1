package vrimplantacao2.vo.cadastro.desmembramento;

import vrimplantacao2.vo.enums.SituacaoCadastro;

public class DesmembramentoVO {

    private int id;
    private int idProduto;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private double percentualEstoque;
    private double percentualPerda = 0;
    private double percentualDesossa = 0;
    private double percentualCusto = 0;

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
    
    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }
    
    public void setSituacaoCadastro (SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO;
    }
    
    public double getPercentualEstoque() {
        return percentualEstoque;
    }

    public void setPercentualEstoque(double percentualEstoque) {
        this.percentualEstoque = percentualEstoque;
    }

    public double getPercentualPerda() {
        return percentualPerda;
    }

    public void setPercentualperda(double percentualPerda) {
        this.percentualPerda = percentualPerda;
    }

    public double getPercentualdesossa() {
        return percentualDesossa;
    }

    public void setPercentualdesossa(double percentualDesossa) {
        this.percentualDesossa = percentualDesossa;
    }

    public double getPercentualcusto() {
        return percentualCusto;
    }

    public void setPercentualcusto(double percentualCusto) {
        this.percentualCusto = percentualCusto;
    }
}
