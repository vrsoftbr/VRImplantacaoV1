package vrimplantacao2_5.Financeiro.VO;

public class TipoPlanoContaVO {

    private Integer id;
    private Integer planoConta1;
    private String planoConta2 = null;
    private Integer nivel;
    private String descricao;

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
     * @return the planoConta1
     */
    public Integer getPlanoConta1() {
        return planoConta1;
    }

    /**
     * @param planoConta1 the planoConta1 to set
     */
    public void setPlanoConta1(Integer planoConta1) {
        this.planoConta1 = planoConta1;
    }

    /**
     * @return the nivel
     */
    public Integer getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Integer nivel) {
        this.nivel = nivel;
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
     * @return the planoConta2
     */
    public String getPlanoConta2() {
        return planoConta2;
    }

    /**
     * @param planoConta2 the planoConta2 to set
     */
    public void setPlanoConta2(String planoConta2) {
        this.planoConta2 = planoConta2;
    }

}
