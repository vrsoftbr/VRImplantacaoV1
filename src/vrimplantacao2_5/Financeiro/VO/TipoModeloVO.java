package vrimplantacao2_5.Financeiro.VO;

public class TipoModeloVO {

    private Integer id;
    private Integer id_tipoMarca;
    private String descricao;
    private String codigoM;
    private boolean sat;

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
     * @return the id_tipoMarca
     */
    public Integer getId_tipoMarca() {
        return id_tipoMarca;
    }

    /**
     * @param id_tipoMarca the id_tipoMarca to set
     */
    public void setId_tipoMarca(Integer id_tipoMarca) {
        this.id_tipoMarca = id_tipoMarca;
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
     * @return the codigoM
     */
    public String getCodigoM() {
        return codigoM;
    }

    /**
     * @param codigoM the codigoM to set
     */
    public void setCodigoM(String codigoM) {
        this.codigoM = codigoM;
    }

    /**
     * @return the sat
     */
    public boolean isSat() {
        return sat;
    }

    /**
     * @param sat the sat to set
     */
    public void setSat(boolean sat) {
        this.sat = sat;
    }

}
