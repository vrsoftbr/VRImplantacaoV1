package vrimplantacao2_5.Financeiro.IMP;

/**
 *
 * @author Bruno
 */
public class RecebivelConfiguracaoTabelaIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private Integer id_recebivelConfiguracao;
    private Integer quantidadeDeDia;
    private boolean utilizaRegra;
    private boolean utilizaDataCorte;
    private Integer id_tipoVencimentoRecebivel;
    private boolean diasUteis;
    private boolean proximoDiaUtil;

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
     * @return the id_recebivelConfiguracao
     */
    public Integer getId_recebivelConfiguracao() {
        return id_recebivelConfiguracao;
    }

    /**
     * @param id_recebivelConfiguracao the id_recebivelConfiguracao to set
     */
    public void setId_recebivelConfiguracao(Integer id_recebivelConfiguracao) {
        this.id_recebivelConfiguracao = id_recebivelConfiguracao;
    }

    /**
     * @return the quantidadeDeDia
     */
    public Integer getQuantidadeDeDia() {
        return quantidadeDeDia;
    }

    /**
     * @param quantidadeDeDia the quantidadeDeDia to set
     */
    public void setQuantidadeDeDia(Integer quantidadeDeDia) {
        this.quantidadeDeDia = quantidadeDeDia;
    }

    /**
     * @return the utilizaRegra
     */
    public boolean isUtilizaRegra() {
        return utilizaRegra;
    }

    /**
     * @param utilizaRegra the utilizaRegra to set
     */
    public void setUtilizaRegra(boolean utilizaRegra) {
        this.utilizaRegra = utilizaRegra;
    }

    /**
     * @return the utilizaDataCorte
     */
    public boolean isUtilizaDataCorte() {
        return utilizaDataCorte;
    }

    /**
     * @param utilizaDataCorte the utilizaDataCorte to set
     */
    public void setUtilizaDataCorte(boolean utilizaDataCorte) {
        this.utilizaDataCorte = utilizaDataCorte;
    }

    /**
     * @return the id_tipoVencimentoRecebivel
     */
    public Integer getId_tipoVencimentoRecebivel() {
        return id_tipoVencimentoRecebivel;
    }

    /**
     * @param id_tipoVencimentoRecebivel the id_tipoVencimentoRecebivel to set
     */
    public void setId_tipoVencimentoRecebivel(Integer id_tipoVencimentoRecebivel) {
        this.id_tipoVencimentoRecebivel = id_tipoVencimentoRecebivel;
    }

    /**
     * @return the diasUteis
     */
    public boolean isDiasUteis() {
        return diasUteis;
    }

    /**
     * @param diasUteis the diasUteis to set
     */
    public void setDiasUteis(boolean diasUteis) {
        this.diasUteis = diasUteis;
    }

    /**
     * @return the proximoDiaUtil
     */
    public boolean isProximoDiaUtil() {
        return proximoDiaUtil;
    }

    /**
     * @param proximoDiaUtil the proximoDiaUtil to set
     */
    public void setProximoDiaUtil(boolean proximoDiaUtil) {
        this.proximoDiaUtil = proximoDiaUtil;
    }

}
