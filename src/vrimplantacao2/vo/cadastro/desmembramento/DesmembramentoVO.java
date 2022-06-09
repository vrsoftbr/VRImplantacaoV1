package vrimplantacao2.vo.cadastro.desmembramento;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class DesmembramentoVO {

    private int id;
    private int idProduto;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private double percentualEstoque;
    private double percentualPerda = 0;
    private double percentualDesossa = 0;
    private double percentualCusto = 0;
    public List<DesmembramentoItemVO> dProduto = new ArrayList();

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

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
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

    public List<DesmembramentoItemVO> getdProduto() {
        return dProduto;
    }

    public void setdProduto(List<DesmembramentoItemVO> dProduto) {
        this.dProduto = dProduto;
    }
   
}
