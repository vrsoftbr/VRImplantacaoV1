package vrimplantacao2.vo.cadastro.desmembramento;

public class DesmembramentoVO {
    
    private String impLoja;
    private String impSistema;
    private String id;
    private String produtoPai;
    private int idConexao = 0;
    
    public String getImpLoja() {
        return impLoja;
    }
    
    public void setImpLoja(String impLoja) {
        this.impLoja = impLoja;
    }
    
     public String getImpSistema() {
        return impSistema;
    }
    
    public void setImpSistema(String impSistema) {
        this.impSistema = impSistema;
    }
    
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
    
    public int getIdConexao() {
        return idConexao;
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }
}
