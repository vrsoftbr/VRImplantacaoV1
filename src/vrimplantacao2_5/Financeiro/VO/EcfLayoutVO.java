package vrimplantacao2_5.Financeiro.VO;

public class EcfLayoutVO {

    private Integer id;
    private Integer id_ecf;
    private Integer id_tecladoLayout;
    private Integer id_finalizadoraLayout;
    private Integer id_acumuladorLayout;
    private Integer id_aliquotaLayout;
    private String regraCalculo;
    private boolean arredondamentoAbnt;

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
     * @return the id_ecf
     */
    public Integer getId_ecf() {
        return id_ecf;
    }

    /**
     * @param id_ecf the id_ecf to set
     */
    public void setId_ecf(Integer id_ecf) {
        this.id_ecf = id_ecf;
    }

    /**
     * @return the id_tecladoLayout
     */
    public Integer getId_tecladoLayout() {
        return id_tecladoLayout;
    }

    /**
     * @param id_tecladoLayout the id_tecladoLayout to set
     */
    public void setId_tecladoLayout(Integer id_tecladoLayout) {
        this.id_tecladoLayout = id_tecladoLayout;
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
     * @return the id_acumuladorLayout
     */
    public Integer getId_acumuladorLayout() {
        return id_acumuladorLayout;
    }

    /**
     * @param id_acumuladorLayout the id_acumuladorLayout to set
     */
    public void setId_acumuladorLayout(Integer id_acumuladorLayout) {
        this.id_acumuladorLayout = id_acumuladorLayout;
    }

    /**
     * @return the id_aliquotaLayout
     */
    public Integer getId_aliquotaLayout() {
        return id_aliquotaLayout;
    }

    /**
     * @param id_aliquotaLayout the id_aliquotaLayout to set
     */
    public void setId_aliquotaLayout(Integer id_aliquotaLayout) {
        this.id_aliquotaLayout = id_aliquotaLayout;
    }

    /**
     * @return the regraCalculo
     */
    public String getRegraCalculo() {
        return regraCalculo;
    }

    /**
     * @param regraCalculo the regraCalculo to set
     */
    public void setRegraCalculo(String regraCalculo) {
        this.regraCalculo = regraCalculo;
    }

    /**
     * @return the arredondamentoAbnt
     */
    public boolean isArredondamentoAbnt() {
        return arredondamentoAbnt;
    }

    /**
     * @param arredondamentoAbnt the arredondamentoAbnt to set
     */
    public void setArredondamentoAbnt(boolean arredondamentoAbnt) {
        this.arredondamentoAbnt = arredondamentoAbnt;
    }

}
