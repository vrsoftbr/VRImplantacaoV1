package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Implantacao
 */
 public class TipoRecebivelIMP {

    private String importSistema;
    private String importLoja;

    private int id;
    private String descricao;
    private int percentual ;
    private int id_tipoTef;

    private int id_tipotiket ;
    private boolean geraRecebimento ;
    private int id_ContaContabilFiscalDebito;
    private int id_ContaContabilFiscalCredito;
    private int id_HistoricoPadrao;
    private SituacaoCadastro situacaoCadastro;
    private int id_TipoVistaPrazo;
    private int id_TipoCartaoTef;
    private int id_Fornecedor;
    private boolean tef;
    private int id_TipoRecebimento ;
    private boolean contabiliza;
    private int id_contaContabilFinanceiro;

    /**
     * @return the importSistema
     */
    public String getImportSistema() {
        return importSistema;
    }

    /**
     * @param importSistema the importSistema to set
     */
    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    /**
     * @return the importLoja
     */
    public String getImportLoja() {
        return importLoja;
    }

    /**
     * @param importLoja the importLoja to set
     */
    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the percentual
     */
    public Integer getPercentual() {
        return percentual;
    }

    /**
     * @param percentual the percentual to set
     */
    public void setPercentual(Integer percentual) {
        this.percentual = percentual;
    }

    /**
     * @return the id_tipotiket
     */
    public Integer getId_tipotiket() {
        return id_tipotiket;
    }

    /**
     * @param id_tipotiket the id_tipotiket to set
     */
    public void setId_tipotiket(Integer id_tipotiket) {
        this.id_tipotiket = id_tipotiket;
    }

    /**
     * @return the geraRecebimento
     */
    public boolean isGeraRecebimento() {
        return geraRecebimento;
    }

    /**
     * @param geraRecebimento the geraRecebimento to set
     */
    public void setGeraRecebimento(boolean geraRecebimento) {
        this.geraRecebimento = geraRecebimento;
    }

    /**
     * @return the id_ContaContabilFiscalDebito
     */
    public Integer getId_ContaContabilFiscalDebito() {
        return id_ContaContabilFiscalDebito;
    }

    /**
     * @param id_ContaContabilFiscalDebito the id_ContaContabilFiscalDebito to
     * set
     */
    public void setId_ContaContabilFiscalDebito(Integer id_ContaContabilFiscalDebito) {
        this.id_ContaContabilFiscalDebito = id_ContaContabilFiscalDebito;
    }

    /**
     * @return the id_ContaContabilFiscalCredito
     */
    public Integer getId_ContaContabilFiscalCredito() {
        return id_ContaContabilFiscalCredito;
    }

    /**
     * @param id_ContaContabilFiscalCredito the id_ContaContabilFiscalCredito to
     * set
     */
    public void setId_ContaContabilFiscalCredito(Integer id_ContaContabilFiscalCredito) {
        this.id_ContaContabilFiscalCredito = id_ContaContabilFiscalCredito;
    }

    /**
     * @return the id_HistoricoPadrao
     */
    public Integer getId_HistoricoPadrao() {
        return id_HistoricoPadrao;
    }

    /**
     * @param id_HistoricoPadrao the id_HistoricoPadrao to set
     */
    public void setId_HistoricoPadrao(Integer id_HistoricoPadrao) {
        this.id_HistoricoPadrao = id_HistoricoPadrao;
    }

    /**
     * @return the situacaoCadastro
     */
    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    /**
     * @param situacaoCadastro the situacaoCadastro to set
     */
    public void setSituacaoCadastro(int situacaoCadastro) {
        this.situacaoCadastro = SituacaoCadastro.getById(situacaoCadastro);
    }

    /**
     * @return the id_TipoVistaPrazo
     */
    public Integer getId_TipoVistaPrazo() {
        return id_TipoVistaPrazo;
    }

    /**
     * @param id_TipoVistaPrazo the id_TipoVistaPrazo to set
     */
    public void setId_TipoVistaPrazo(Integer id_TipoVistaPrazo) {
        this.id_TipoVistaPrazo = id_TipoVistaPrazo;
    }

    /**
     * @return the id_TipoCartaoTef
     */
    public Integer getId_TipoCartaoTef() {
        return id_TipoCartaoTef;
    }

    /**
     * @param id_TipoCartaoTef the id_TipoCartaoTef to set
     */
    public void setId_TipoCartaoTef(Integer id_TipoCartaoTef) {
        this.id_TipoCartaoTef = id_TipoCartaoTef;
    }

    /**
     * @return the id_Fornecedor
     */
    public Integer getId_Fornecedor() {
        return id_Fornecedor;
    }

    /**
     * @param id_Fornecedor the id_Fornecedor to set
     */
    public void setId_Fornecedor(Integer id_Fornecedor) {
        this.id_Fornecedor = id_Fornecedor;
    }

    /**
     * @return the tef
     */
    public boolean isTef() {
        return tef;
    }

    /**
     * @param tef the tef to set
     */
    public void setTef(boolean tef) {
        this.tef = tef;
    }

    /**
     * @return the id_TipoRecebimento
     */
    public Integer getId_TipoRecebimento() {
        return id_TipoRecebimento;
    }

    /**
     * @param id_TipoRecebimento the id_TipoRecebimento to set
     */
    public void setId_TipoRecebimento(Integer id_TipoRecebimento) {
        this.id_TipoRecebimento = id_TipoRecebimento;
    }

    /**
     * @return the contabiliza
     */
    public boolean isContabiliza() {
        return contabiliza;
    }

    /**
     * @param contabiliza the contabiliza to set
     */
    public void setContabiliza(boolean contabiliza) {
        this.contabiliza = contabiliza;
    }

    /**
     * @return the id_contaContabilFinanceiro
     */
    public Integer getId_contaContabilFinanceiro() {
        return id_contaContabilFinanceiro;
    }

    /**
     * @param id_contaContabilFinanceiro the id_contaContabilFinanceiro to set
     */
    public void setId_contaContabilFinanceiro(Integer id_contaContabilFinanceiro) {
        this.id_contaContabilFinanceiro = id_contaContabilFinanceiro;
    }

    /**
     * @return the id_tipoTef
     */
    public Integer getId_tipoTef() {
        return id_tipoTef;
    }

    /**
     * @param id_tipoTef the id_tipoTef to set
     */
    public void setId_tipoTef(Integer id_tipoTef) {
        this.id_tipoTef = id_tipoTef;
    }

    
}
