package vrimplantacao2_5.tipoRecebivel.IMP;

/**
 *
 * @author Bruno
 */
public class TipoSaidaNotaFiscalSequenciaIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private Integer id_loja;
    private Integer id_tipoSaida;
    private Integer id_notaSaidaSequencia;

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
     * @return the id_tipoSaida
     */
    public Integer getId_tipoSaida() {
        return id_tipoSaida;
    }

    /**
     * @param id_tipoSaida the id_tipoSaida to set
     */
    public void setId_tipoSaida(Integer id_tipoSaida) {
        this.id_tipoSaida = id_tipoSaida;
    }

    /**
     * @return the id_loja
     */
    public Integer getId_loja() {
        return id_loja;
    }

    /**
     * @param id_loja the id_loja to set
     */
    public void setId_loja(Integer id_loja) {
        this.id_loja = id_loja;
    }

    /**
     * @return the id_notaSaidaSequencia
     */
    public Integer getId_notaSaidaSequencia() {
        return id_notaSaidaSequencia;
    }

    /**
     * @param id_notaSaidaSequencia the id_notaSaidaSequencia to set
     */
    public void setId_notaSaidaSequencia(Integer id_notaSaidaSequencia) {
        this.id_notaSaidaSequencia = id_notaSaidaSequencia;
    }

}
