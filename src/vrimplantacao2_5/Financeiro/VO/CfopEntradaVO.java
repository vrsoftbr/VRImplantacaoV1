package vrimplantacao2_5.Financeiro.VO;

public class CfopEntradaVO {

    private Integer id;
    private String cfop;
    private Integer id_tipoEntrada;

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
