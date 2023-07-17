package vrimplantacao2.vo.cadastro;

import vrimplantacao2.vo.enums.SituacaoCadastro;

public class TipoRecebivelVO {

    private int id ;
    private String descricao ;
    private int percentual;
    private int id_TipoTef ;
    private int id_TipoTicket ;
    private boolean geraRecebimento;
    private int id_contaContabilFiscalDebito ;
    private int id_ContaContabilFiscalCredito ;
    private int id_HistoricoPadrao ;
    private SituacaoCadastro id_situacaoCadastro;
    private int id_TipoPrazo ;
    private int id_TipoCartaoTef ;
    private int id_Fornecedor ;
    private boolean tef;
    private int id_tiporecebimento ;
    private boolean contabiliza ;
    private int id_ContaContabilFinanceiro ;
    
    public TipoRecebivelVO (){     
    }
 
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
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
     * @return the id_TipoTef
     */
    public Integer getId_TipoTef() {
        return id_TipoTef;
    }

    /**
     * @param id_TipoTef the id_TipoTef to set
     */
    public void setId_TipoTef(Integer id_TipoTef) {
        this.id_TipoTef = id_TipoTef;
    }

    /**
     * @return the id_TipoTicket
     */
    public Integer getId_TipoTicket() {
        return id_TipoTicket;
    }

    /**
     * @param id_TipoTicket the id_TipoTicket to set
     */
    public void setId_TipoTicket(Integer id_TipoTicket) {
        this.id_TipoTicket = id_TipoTicket;
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
     * @return the id_contaContabilFiscalDebito
     */
    public Integer getId_contaContabilFiscalDebito() {
        return id_contaContabilFiscalDebito;
    }

    /**
     * @param id_contaContabilFiscalDebito the id_contaContabilFiscalDebito to set
     */
    public void setId_contaContabilFiscalDebito(Integer id_contaContabilFiscalDebito) {
        this.id_contaContabilFiscalDebito = id_contaContabilFiscalDebito;
    }

    /**
     * @return the id_ContaContabilFiscalCredito
     */
    public Integer getId_ContaContabilFiscalCredito() {
        return id_ContaContabilFiscalCredito;
    }

    /**
     * @param id_ContaContabilFiscalCredito the id_ContaContabilFiscalCredito to set
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
     * @return the id_TipoPrazo
     */
    public Integer getId_TipoPrazo() {
        return id_TipoPrazo;
    }

    /**
     * @param id_TipoPrazo the id_TipoPrazo to set
     */
    public void setId_TipoPrazo(Integer id_TipoPrazo) {
        this.id_TipoPrazo = id_TipoPrazo;
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
     * @return the id_tiporecebimento
     */
    public Integer getId_tiporecebimento() {
        return id_tiporecebimento;
    }

    /**
     * @param id_tiporecebimento the id_tiporecebimento to set
     */
    public void setId_tiporecebimento(Integer id_tiporecebimento) {
        this.id_tiporecebimento = id_tiporecebimento;
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
     * @return the id_ContaContabilFinanceiro
     */
    public Integer getId_ContaContabilFinanceiro() {
        return id_ContaContabilFinanceiro;
    }

    /**
     * @param id_ContaContabilFinanceiro the id_ContaContabilFinanceiro to set
     */
    public void setId_ContaContabilFinanceiro(Integer id_ContaContabilFinanceiro) {
        this.id_ContaContabilFinanceiro = id_ContaContabilFinanceiro;
    }

    /**
     * @return the id_situacaoCadastro
     */
    public SituacaoCadastro getId_situacaoCadastro() {
        return id_situacaoCadastro;
    }

    /**
     * @param id_situacaoCadastro the id_situacaoCadastro to set
     */
    public void setId_situacaoCadastro(SituacaoCadastro id_situacaoCadastro) {
        this.id_situacaoCadastro = id_situacaoCadastro;
    }

}

    
    