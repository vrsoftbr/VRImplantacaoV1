package vrimplantacao2.vo.importacao;

import java.util.Date;

/**
 *
 * @author Leandro
 */
public class ProdutoFornecedorIMP {

    private String importSistema;
    private String importLoja;
    private String idFornecedor;
    private String idProduto;
    private String codigoExterno;
    private Date dataAlteracao = new Date();
    private double qtdEmbalagem = 1;
    private double pesoEmbalagem = 0;
    private double custoTabela = 0;
    private double ipi = 0;
    private int tipoIpi = 0;
    private String idDivisaoFornecedor = "";
    
    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }
    
    public String getIdFornecedor() {
        return idFornecedor;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public double getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public double getPesoEmbalagem() {
        return pesoEmbalagem;
    }    

    public double getCustoTabela() {
        return custoTabela;
    }
    
    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public void setIdFornecedor(String idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public void setQtdEmbalagem(double qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public void setPesoEmbalagem(double pesoEmbalagem) {
        this.pesoEmbalagem = pesoEmbalagem;
    }

    public void setCustoTabela(double custoTabela) {
        this.custoTabela = custoTabela;
    }

    public double getIpi() {
        return ipi;
    }

    public void setIpi(double ipi) {
        this.ipi = ipi;
    }

    public int getTipoIpi() {
        return tipoIpi;
    }

    public void setTipoIpi(int tipoIpi) {
        this.tipoIpi = tipoIpi;
    }

    public String getIdDivisaoFornecedor() {
        return idDivisaoFornecedor;
    }

    public void setIdDivisaoFornecedor(String idDivisaoFornecedor) {
        this.idDivisaoFornecedor = idDivisaoFornecedor;
    }
    
}
