package vrimplantacao2_5.Financeiro.VO;

public class CfopSaidaVO {

    private Integer id;
    private String cfop;
    private int id_tipoSaida;

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

    /**
     * @return the id_tipoSaida
     */
    public Integer getId_tipoSaida() {
        return id_tipoSaida;
    }

    /**
     * @param id_tipoSaida the id_tipoSaida to set
     */
    public void setId_tipoSaida(int id_tipoSaida) {
        this.id_tipoSaida = id_tipoSaida;
    }

}
