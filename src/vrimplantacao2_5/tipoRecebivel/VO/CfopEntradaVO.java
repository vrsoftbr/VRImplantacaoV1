package vrimplantacao2_5.tipoRecebivel.VO;

import vrimplantacao2.vo.enums.SituacaoCadastro;

public class CfopEntradaVO {

    private Integer id;
    private Integer cfop;
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
     * @return the cfop
     */
    public Integer getCfop() {
        return cfop;
    }

    /**
     * @param cfop the cfop to set
     */
    public void setCfop(Integer cfop) {
        this.cfop = cfop;
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

}
