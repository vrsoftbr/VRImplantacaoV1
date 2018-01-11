package vrimplantacao2.vo.cadastro.fiscal.pautafiscal;

import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.TipoIva;

/**
 * Classe que representa um registro na tabela pauta fiscal.
 * @author Leandro
 */
public class PautaFiscalVO {
    
    private int id;// integer NOT NULL DEFAULT nextval('pautafiscal_id_seq'::regclass),
    private int ncm1;// integer NOT NULL,
    private int ncm2;// integer NOT NULL,
    private int ncm3;// integer NOT NULL,
    private int excecao = 1;// integer NOT NULL,
    private int id_estado;// integer NOT NULL,
    private double iva;// numeric(13,4) NOT NULL,
    private TipoIva tipoIva = TipoIva.PERCENTUAL;// integer NOT NULL,
    private int id_aliquotaCredito;// integer NOT NULL,
    private int id_aliquotaDebito;// integer NOT NULL,
    private int id_aliquotaDebitoForaEstado;// integer NOT NULL DEFAULT 0,
    private double ivaAjustado = 0;// numeric(13,4) NOT NULL DEFAULT 0,
    private boolean icmsRecolhidoAntecipadamente = false;// boolean NOT NULL DEFAULT false,
    private int id_aliquotaCreditoForaEstado;// integer,

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNcm1() {
        return ncm1;
    }

    public void setNcm1(int ncm1) {
        this.ncm1 = ncm1;
    }

    public int getNcm2() {
        return ncm2;
    }

    public void setNcm2(int ncm2) {
        this.ncm2 = ncm2;
    }

    public int getNcm3() {
        return ncm3;
    }

    public void setNcm3(int ncm3) {
        this.ncm3 = ncm3;
    }

    public int getExcecao() {
        return excecao;
    }

    public void setExcecao(int excecao) {
        this.excecao = excecao;
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = MathUtils.round(iva, 4, 9999999);
    }

    public TipoIva getTipoIva() {
        return tipoIva;
    }

    public void setTipoIva(TipoIva tipoIva) {
        this.tipoIva = tipoIva != null ? tipoIva : TipoIva.PERCENTUAL;
    }

    public int getId_aliquotaCredito() {
        return id_aliquotaCredito;
    }

    public void setId_aliquotaCredito(int id_aliquotaCredito) {
        this.id_aliquotaCredito = id_aliquotaCredito;
    }

    public int getId_aliquotaDebito() {
        return id_aliquotaDebito;
    }

    public void setId_aliquotaDebito(int id_aliquotaDebito) {
        this.id_aliquotaDebito = id_aliquotaDebito;
    }

    public int getId_aliquotaDebitoForaEstado() {
        return id_aliquotaDebitoForaEstado;
    }

    public void setId_aliquotaDebitoForaEstado(int id_aliquotaDebitoForaEstado) {
        this.id_aliquotaDebitoForaEstado = id_aliquotaDebitoForaEstado;
    }

    public double getIvaAjustado() {
        return ivaAjustado;
    }

    public void setIvaAjustado(double ivaAjustado) {
        this.ivaAjustado = MathUtils.round(ivaAjustado, 4, 99999999);
    }

    public boolean isIcmsRecolhidoAntecipadamente() {
        return icmsRecolhidoAntecipadamente;
    }

    public void setIcmsRecolhidoAntecipadamente(boolean icmsRecolhidoAntecipadamente) {
        this.icmsRecolhidoAntecipadamente = icmsRecolhidoAntecipadamente;
    }

    public int getId_aliquotaCreditoForaEstado() {
        return id_aliquotaCreditoForaEstado;
    }

    public void setId_aliquotaCreditoForaEstado(int id_aliquotaCreditoForaEstado) {
        this.id_aliquotaCreditoForaEstado = id_aliquotaCreditoForaEstado;
    }
    
    
}
