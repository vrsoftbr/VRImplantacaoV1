package vrimplantacao2_5.Financeiro.IMP;

/**
 *
 * @author Bruno
 */
public class ContabilidadeTipoEntradaIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private Integer id_tipoValor;
    private Integer id_contaContabilCredito;
    private Integer id_contaContaContabilDebito;
    private Integer id_historicoPadrao;

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
     * @return the id_tipoValor
     */
    public Integer getId_tipoValor() {
        return id_tipoValor;
    }

    /**
     * @param id_tipoValor the id_tipoValor to set
     */
    public void setId_tipoValor(Integer id_tipoValor) {
        this.id_tipoValor = id_tipoValor;
    }

   
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
     * @return the id_contaContabilCredito
     */
    public Integer getId_contaContabilCredito() {
        return id_contaContabilCredito;
    }

    /**
     * @param id_contaContabilCredito the id_contaContabilCredito to set
     */
    public void setId_contaContabilCredito(Integer id_contaContabilCredito) {
        this.id_contaContabilCredito = id_contaContabilCredito;
    }

    /**
     * @return the id_contaContaContabilDebito
     */
    public Integer getId_contaContaContabilDebito() {
        return id_contaContaContabilDebito;
    }

    /**
     * @param id_contaContaContabilDebito the id_contaContaContabilDebito to set
     */
    public void setId_contaContaContabilDebito(Integer id_contaContaContabilDebito) {
        this.id_contaContaContabilDebito = id_contaContaContabilDebito;
    }

}
