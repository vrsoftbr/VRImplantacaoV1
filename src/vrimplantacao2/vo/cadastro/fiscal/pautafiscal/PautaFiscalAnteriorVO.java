package vrimplantacao2.vo.cadastro.fiscal.pautafiscal;

/**
 *
 * @author Leandro
 */
public class PautaFiscalAnteriorVO {
    private String sistema;
    private String loja;
    private String id;
    private PautaFiscalVO codigoAtual;
    private String cstDebito;
    private String cstCredito;
    private double iva;
    private double ivaAjustado;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PautaFiscalVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(PautaFiscalVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getCstDebito() {
        return cstDebito;
    }

    public void setCstDebito(String cstDebito) {
        this.cstDebito = cstDebito;
    }

    public String getCstCredito() {
        return cstCredito;
    }

    public void setCstCredito(String cstCredito) {
        this.cstCredito = cstCredito;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getIvaAjustado() {
        return ivaAjustado;
    }

    public void setIvaAjustado(double ivaAjustado) {
        this.ivaAjustado = ivaAjustado;
    }

    @Override
    public String toString() {
        return "PautaFiscalAnteriorVO{" + "sistema=" + sistema + ", loja=" + loja + ", id=" + id + ", codigoAtual=" + codigoAtual + ", cstDebito=" + cstDebito + ", cstCredito=" + cstCredito + ", iva=" + iva + ", ivaAjustado=" + ivaAjustado + '}';
    }
    
}
