package vrimplantacao2_5.Financeiro.IMP;

/**
 *
 * @author Bruno
 */
public class CfopEntradaIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String cfop;
    private Integer id_tipoEntrada;

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
     * @return the id_tipoEntrada
     */
    public Integer getId_tipoEntrada() {
        return id_tipoEntrada;
    }

    /**
     * @param id_tipoEntrada the id_tipoEntrada to set
     */
    public void setId_tipoEntrada(Integer id_tipoEntrada) {
        this.id_tipoEntrada = id_tipoEntrada;
    }

    /**
     * @return the cfop
     */
    public String getCfop() {
        return cfop;
    }

    /**
     * @param cfop the cfop to set
     */
    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

}
