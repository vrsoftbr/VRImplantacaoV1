package vrimplantacao2.vo.importacao;

import java.util.List;

public class DesmembramentoIMP {

    private String impId;
    private String produtoPai;
    private String produtoFilho;
    private Double percentual;

    public String getImpId() {
        return impId;
    }

    public void setImpId(String impId) {
        this.impId = impId;
    }

    public String getProdutoPai() {
        return produtoPai;
    }

    public void setProdutoPai(String produtoPai) {
        this.produtoPai = produtoPai;
    }

    public String getProdutoFilho() {
        return produtoFilho;
    }

    public void setProdutoFilho(String produtoFilho) {
        this.produtoFilho = produtoFilho;
    }
    
    public Double getPercentual() {
        return percentual;
    }
    
    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }
}
