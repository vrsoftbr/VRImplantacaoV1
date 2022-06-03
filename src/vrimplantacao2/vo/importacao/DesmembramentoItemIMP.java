package vrimplantacao2.vo.importacao;

public class DesmembramentoItemIMP {

    private String id;
    private DesmembramentoIMP produtoPai;
    private String produtoFilho;
    private double estoque;

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public DesmembramentoIMP getDesmembramento() {
        return produtoPai;
    }
    
    public void setDesmembramento (DesmembramentoIMP produtoPai){
        this.produtoPai = produtoPai;
    }
    
    public String getProdutoFilho() {
        return produtoFilho;
    }
    
    public void setProdutoFilho(String produtoFilho) {
        this.produtoFilho = produtoFilho;
    }
    
    public double getEstoque() {
        return estoque;
    }

    public void setEstoque(double estoque) {
        this.estoque = estoque;
    }
}
