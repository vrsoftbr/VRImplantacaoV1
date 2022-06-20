package vrimplantacao2.vo.importacao;

public class DesmembramentoIMP {

    private String id;
    private String produtoPai;
    private String produtoFilho;
    private Double percentual;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
