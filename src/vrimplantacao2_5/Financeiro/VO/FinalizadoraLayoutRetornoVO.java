package vrimplantacao2_5.Financeiro.VO;

public class FinalizadoraLayoutRetornoVO {

    private Integer id;
    private Integer id_finalizadoraLayout;
    private Integer id_finalizadora;
    private String retorno;
    private boolean utilizado;

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
     * @return the id_finalizadoraLayout
     */
    public Integer getId_finalizadoraLayout() {
        return id_finalizadoraLayout;
    }

    /**
     * @param id_finalizadoraLayout the id_finalizadoraLayout to set
     */
    public void setId_finalizadoraLayout(Integer id_finalizadoraLayout) {
        this.id_finalizadoraLayout = id_finalizadoraLayout;
    }

    /**
     * @return the id_finalizadora
     */
    public Integer getId_finalizadora() {
        return id_finalizadora;
    }

    /**
     * @param id_finalizadora the id_finalizadora to set
     */
    public void setId_finalizadora(Integer id_finalizadora) {
        this.id_finalizadora = id_finalizadora;
    }

    /**
     * @return the retorno
     */
    public String getRetorno() {
        return retorno;
    }

    /**
     * @param retorno the retorno to set
     */
    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

    /**
     * @return the utilizado
     */
    public boolean isUtilizado() {
        return utilizado;
    }

    /**
     * @param utilizado the utilizado to set
     */
    public void setUtilizado(boolean utilizado) {
        this.utilizado = utilizado;
    }


}
