package vrimplantacao2.vo.cadastro.desmembramento;

public class DesmembramentoAnteriorVO {
    
    private String sistema;
    private String loja;
    private String importId;
    private Integer codigoAtual;
    private String produto;
    private double quantidade;
    private String descricao;
    private int idConexao;
 
    public String getSistema() {
        return sistema;
    }
    
    public void setSistema(String sistema) {
        this.sistema = sistema;
    }
    
    public String getLoja() {
        return loja;
    }
    
    public void setLoja(String loja) {
        this.loja = loja;
    }
    
    public String getImportId() {
        return importId;
    }
    
    public void setImportId(String importId) {
        this.importId = importId;
    }
    
    public Integer getCodigoAtual() {
        return codigoAtual;
    } 
    
    public void setCodigoAtual(Integer codigoAtual){
        this.codigoAtual = codigoAtual;
    }
    
    public String getProduto() {
        return produto;
    }
    
    public void setProduto(String produto){
        this.produto = produto;
    }
    
    public double getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public int getIdConexao() {
        return idConexao;
    }
    
    public void setIdConexao(int idConexao){
        this.idConexao = idConexao;
    }
}