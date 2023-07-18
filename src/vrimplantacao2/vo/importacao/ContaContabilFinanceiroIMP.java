package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Bruno
 */
public class ContaContabilFinanceiroIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String descricao;
    private SituacaoCadastro id_situacaoCadastro;
    private Integer id_contaContabilFiscal;
    private boolean transferencia;
    private Integer id_historicoPadrao;
    private boolean contabiliza;
    private Integer id_tipoCentroCusto;

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

    /**
     * @return the id_contaContabilFiscal
     */
    public Integer getId_contaContabilFiscal() {
        return id_contaContabilFiscal;
    }

    /**
     * @param id_contaContabilFiscal the id_contaContabilFiscal to set
     */
    public void setId_contaContabilFiscal(Integer id_contaContabilFiscal) {
        this.id_contaContabilFiscal = id_contaContabilFiscal;
    }

    /**
     * @return the transferencia
     */
    public boolean isTransferencia() {
        return transferencia;
    }

    /**
     * @param transferencia the transferencia to set
     */
    public void setTransferencia(boolean transferencia) {
        this.transferencia = transferencia;
    }

    /**
     * @return the id_historicoPadrao
     */
    public Integer getId_historicoPadrao() {
        return id_historicoPadrao;
    }

    /**
     * @param id_historicoPadrao the id_historicoPadrao to set
     */
    public void setId_historicoPadrao(Integer id_historicoPadrao) {
        this.id_historicoPadrao = id_historicoPadrao;
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
     * @return the id_tipoCentroCusto
     */
    public Integer getId_tipoCentroCusto() {
        return id_tipoCentroCusto;
    }

    /**
     * @param id_tipoCentroCusto the id_tipoCentroCusto to set
     */
    public void setId_tipoCentroCusto(Integer id_tipoCentroCusto) {
        this.id_tipoCentroCusto = id_tipoCentroCusto;
    }

}
