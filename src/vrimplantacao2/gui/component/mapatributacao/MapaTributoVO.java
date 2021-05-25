package vrimplantacao2.gui.component.mapatributacao;

import vrimplantacao2.vo.cadastro.tributacao.AliquotaVO;
import vrimplantacao2.vo.enums.Icms;

/**
 *
 * @author Leandro
 */
public class MapaTributoVO {
    private String sistema;
    private String agrupador;
    private String origId;
    private String origDescricao;
    private int origCst;
    private double origAliquota;
    private double origReduzido;
    private double origFcp;
    private boolean origDesonerado;
    private double origPorcentagemDesonerado;
    private Icms aliquota;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }

    public String getOrigId() {
        return origId;
    }

    public void setOrigId(String origId) {
        this.origId = origId;
    }

    public String getOrigDescricao() {
        return origDescricao;
    }

    public void setOrigDescricao(String origDescricao) {
        this.origDescricao = origDescricao;
    }

    public Icms getAliquota() {
        return aliquota;
    }

    public void setAliquota(Icms aliquota) {
        this.aliquota = aliquota;
    }

    public int getOrigCst() {
        return origCst;
    }

    public void setOrigCst(int origCst) {
        this.origCst = origCst;
    }

    public double getOrigAliquota() {
        return origAliquota;
    }

    public void setOrigAliquota(double origAliquota) {
        this.origAliquota = origAliquota;
    }

    public double getOrigReduzido() {
        return origReduzido;
    }

    public void setOrigReduzido(double origReduzido) {
        this.origReduzido = origReduzido;
    }

    public double getOrigFcp() {
        return origFcp;
    }

    public void setOrigFcp(double origFcp) {
        this.origFcp = origFcp;
    }

    public boolean isOrigDesonerado() {
        return origDesonerado;
    }

    public void setOrigDesonerado(boolean origDesonerado) {
        this.origDesonerado = origDesonerado;
    }

    public double getOrigPorcentagemDesonerado() {
        return origPorcentagemDesonerado;
    }

    public void setOrigPorcentagemDesonerado(double origPorcentagemDesonerado) {
        this.origPorcentagemDesonerado = origPorcentagemDesonerado;
    }
    
    public AliquotaVO converterEmVo() {
        AliquotaVO vo = new AliquotaVO();
        
        vo.setId(aliquota != null ? this.aliquota.getId() : -1);
        vo.setDescricao(this.origDescricao);
        vo.setCst(this.origCst);
        vo.setAliquota(this.origAliquota);
        vo.setReduzido(this.origReduzido);
        vo.setFcp(this.origFcp);
        vo.setDesonerado(this.origDesonerado);
        vo.setPorcentagemDesonerado(this.origPorcentagemDesonerado);
        
        return vo;
    }
}
