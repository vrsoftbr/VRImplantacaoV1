package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author Leandro
 */
public final class MercadologicoMapaVO {
    private String sistemaId;
    private String lojaId;
    private String mercadologicoId;
    private String descricao;
    private Integer mercadologicoVR;

    /**
     *
     * @param sistemaId
     * @param lojaId
     * @param mercadologicoId
     * @param descricao
     * @param mercadologicoVR
     */
    public MercadologicoMapaVO(String sistemaId, String lojaId, String mercadologicoId, String descricao, Integer mercadologicoVR) {
        setSistemaId(sistemaId);
        setLojaId(lojaId);
        setMercadologicoId(mercadologicoId);
        setDescricao(descricao);
        setMercadologicoVR(mercadologicoVR);
    }
    
    /**
     *
     * @param sistemaId
     * @param lojaId
     * @param mercadologicoId
     * @param descricao
     */
    public MercadologicoMapaVO(String sistemaId, String lojaId, String mercadologicoId, String descricao) {
        setSistemaId(sistemaId);
        setLojaId(lojaId);
        setMercadologicoId(mercadologicoId);
        setDescricao(descricao);
        setMercadologicoVR(null);
    }

    public MercadologicoMapaVO() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSistemaId() {
        return sistemaId;
    }

    public void setSistemaId(String sistemaId) {
        this.sistemaId = sistemaId;
    }

    public String getLojaId() {
        return lojaId;
    }

    public void setLojaId(String lojaId) {
        this.lojaId = lojaId;
    }

    public String getMercadologicoId() {
        return mercadologicoId;
    }

    public void setMercadologicoId(String mercadologicoId) {
        this.mercadologicoId = mercadologicoId;
    }

    public Integer getMercadologicoVR() {
        return mercadologicoVR;
    }

    public void setMercadologicoVR(Integer object) {
        this.mercadologicoVR = object;
    }
    
    public String getKey() {
        return sistemaId + "-" + lojaId + "-" + mercadologicoId;
    }

    @Override
    public String toString() {
        return "MercadologicoMapaVO{" + "sistemaId=" + sistemaId + ", lojaId=" + lojaId + ", mercadologicoId=" + mercadologicoId + ", descricao=" + descricao + ", mercadologicoVR=" + mercadologicoVR + '}';
    }
    
}
