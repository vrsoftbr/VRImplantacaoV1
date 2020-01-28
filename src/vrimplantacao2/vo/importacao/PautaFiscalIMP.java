package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.cadastro.tributacao.AliquotaVO;
import vrimplantacao2.vo.enums.TipoIva;

public class PautaFiscalIMP {
    
    private String id;
    private String ncm;
    private String uf;
    private double iva = 0;
    private TipoIva tipoIva = TipoIva.PERCENTUAL;
    private AliquotaVO aliquotaCredito = AliquotaVO.OUTRAS;
    private AliquotaVO aliquotaDebito = AliquotaVO.OUTRAS;
    private AliquotaVO aliquotaDebitoForaEstado = AliquotaVO.OUTRAS;
    private AliquotaVO aliquotaCreditoForaEstado = AliquotaVO.OUTRAS;
    private String aliquotaCreditoId;
    private String aliquotaDebitoId;
    private String aliquotaDebitoForaEstadoId;
    private String aliquotaCreditoForaEstadoId;
    private double ivaAjustado = 0;
    private boolean icmsRecolhidoAntecipadamente = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public TipoIva getTipoIva() {
        return tipoIva;
    }

    public void setTipoIva(TipoIva tipoIva) {
        this.tipoIva = tipoIva != null ? tipoIva : TipoIva.PERCENTUAL;
    }

    public AliquotaVO getAliquotaCredito() {
        return aliquotaCredito;
    }

    public void setAliquotaCredito(AliquotaVO aliquotaCredito) {
        this.aliquotaCredito = aliquotaCredito != null ? aliquotaCredito : AliquotaVO.OUTRAS;
    }
    
    public void setAliquotaCredito(int cst, double aliquota, double reduzido) {
        this.aliquotaCredito = new AliquotaVO(-1, "", cst, aliquota, reduzido);
    }


    public AliquotaVO getAliquotaDebito() {
        return aliquotaDebito;
    }

    public void setAliquotaDebito(AliquotaVO aliquotaDebito) {
        this.aliquotaDebito = aliquotaDebito != null ? aliquotaDebito : AliquotaVO.OUTRAS;
    }
    
    public void setAliquotaDebito(int cst, double aliquota, double reduzido) {
        this.aliquotaDebito = new AliquotaVO(-1, "", cst, aliquota, reduzido);
    }

    public AliquotaVO getAliquotaDebitoForaEstado() {
        return aliquotaDebitoForaEstado;
    }

    public void setAliquotaDebitoForaEstado(AliquotaVO aliquotaDebitoForaEstado) {
        this.aliquotaDebitoForaEstado = aliquotaDebitoForaEstado != null ? aliquotaDebitoForaEstado : AliquotaVO.OUTRAS;;
    }
    
    public void setAliquotaDebitoForaEstado(int cst, double aliquota, double reduzido) {
        this.aliquotaDebitoForaEstado = new AliquotaVO(-1, "", cst, aliquota, reduzido);
    }

    public double getIvaAjustado() {
        return ivaAjustado;
    }

    public void setIvaAjustado(double ivaAjustado) {
        this.ivaAjustado = ivaAjustado;
    }

    public boolean isIcmsRecolhidoAntecipadamente() {
        return icmsRecolhidoAntecipadamente;
    }

    public void setIcmsRecolhidoAntecipadamente(boolean icmsRecolhidoAntecipadamente) {
        this.icmsRecolhidoAntecipadamente = icmsRecolhidoAntecipadamente;
    }

    public String getAliquotaCreditoId() {
        return aliquotaCreditoId;
    }

    public void setAliquotaCreditoId(String aliquotaCreditoId) {
        this.aliquotaCreditoId = aliquotaCreditoId;
    }

    public String getAliquotaDebitoId() {
        return aliquotaDebitoId;
    }

    public void setAliquotaDebitoId(String aliquotaDebitoId) {
        this.aliquotaDebitoId = aliquotaDebitoId;
    }

    public String getAliquotaDebitoForaEstadoId() {
        return aliquotaDebitoForaEstadoId;
    }

    public void setAliquotaDebitoForaEstadoId(String aliquotaDebitoForaEstadoId) {
        this.aliquotaDebitoForaEstadoId = aliquotaDebitoForaEstadoId;
    }

    public AliquotaVO getAliquotaCreditoForaEstado() {
        return aliquotaCreditoForaEstado;
    }

    public void setAliquotaCreditoForaEstado(AliquotaVO aliquotaCreditoForaEstado) {
        this.aliquotaCreditoForaEstado = aliquotaCreditoForaEstado;
    }
    
    public void setAliquotaCreditoForaEstado(int cst, double aliquota, double reduzido) {
        this.aliquotaCreditoForaEstado = new AliquotaVO(-1, "", cst, aliquota, reduzido);
    }

    public String getAliquotaCreditoForaEstadoId() {
        return aliquotaCreditoForaEstadoId;
    }

    public void setAliquotaCreditoForaEstadoId(String aliquotaCreditoForaEstadoId) {
        this.aliquotaCreditoForaEstadoId = aliquotaCreditoForaEstadoId;
    }

    @Override
    public String toString() {
        return "PautaFiscalIMP{" + "id=" + id + ", ncm=" + ncm + ", uf=" + uf + ", iva=" + iva + ", tipoIva=" + tipoIva + ", aliquotaCredito=" + aliquotaCredito + ", aliquotaDebito=" + aliquotaDebito + ", aliquotaDebitoForaEstado=" + aliquotaDebitoForaEstado + ", aliquotaCreditoForaEstado=" + aliquotaCreditoForaEstado + ", aliquotaCreditoId=" + aliquotaCreditoId + ", aliquotaDebitoId=" + aliquotaDebitoId + ", aliquotaDebitoForaEstadoId=" + aliquotaDebitoForaEstadoId + ", aliquotaCreditoForaEstadoId=" + aliquotaCreditoForaEstadoId + ", ivaAjustado=" + ivaAjustado + ", icmsRecolhidoAntecipadamente=" + icmsRecolhidoAntecipadamente + '}';
    }
    
    
        
}
