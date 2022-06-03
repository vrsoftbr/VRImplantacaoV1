package vrimplantacao2.vo.cadastro.desmembramento;

/**
 * Classe que representa um registro da tabela implantacao.codant_desmembramento
 */

public class DesmembramentoAnteriorVO {

    private String sistema;
    private String loja;
    private String impId;
    private Integer codigoAtual;
    private String produtoPai;
    private String produtoFilho;
    private double percentual;
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

    public String getImpId() {
        return impId;
    }

    public void setImpId(String impId) {
        this.impId = impId;
    }

    public Integer getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(Integer codigoAtual) {
        this.codigoAtual = codigoAtual;
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
    
    public double getPercentual() {
        return percentual;
    }

    public void setPercentual(double percentual) {
        this.percentual = percentual;
    }

    public int getIdConexao() {
        return idConexao;
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }
}
