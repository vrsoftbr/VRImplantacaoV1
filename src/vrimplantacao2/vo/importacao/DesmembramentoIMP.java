package vrimplantacao2.vo.importacao;

/**
 *
 * @author Alan
 */
public class DesmembramentoIMP {
    
    private String id;
    private String id_produto;
    private String id_produtofilho;
    private int idSituacaocadastro = 1;
    private double estoque;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId_produtopai() {
        return id_produto;
    }
    
    public void setId_produtopai(String id_produtopai) {
        this.id_produto = id_produto;
    }
    
    public String getId_produtofilho() {
        return id_produtofilho;
    }
    
    public void setId_produtofilho(String id_produtofilho) {
        this.id_produtofilho = id_produtofilho;
    }
    
    public int getIdSituacaocadastro() {
        return idSituacaocadastro;
    }

    public void setIdSituacaocadastro(int idSituacaocadastro) {
        this.idSituacaocadastro = idSituacaocadastro;
    }
    
    public double getEstoque() {
        return estoque;
    }
    
    public void setEstoque(double estoque){
        this.estoque = estoque;
    }
}
