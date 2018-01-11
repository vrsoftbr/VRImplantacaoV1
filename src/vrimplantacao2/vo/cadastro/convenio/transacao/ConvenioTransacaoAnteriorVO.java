package vrimplantacao2.vo.cadastro.convenio.transacao;

import java.util.Date;

/**
 *
 * @author Leandro
 */
public class ConvenioTransacaoAnteriorVO {
    private String sistema;
    private String loja;
    private String id;
    private ConvenioTransacaoVO codigoAtual;
    private Date data;
    private double valor;
    private boolean pago = false;

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(ConvenioTransacaoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getId() {
        return id;
    }

    public ConvenioTransacaoVO getCodigoAtual() {
        return codigoAtual;
    }

    public Date getData() {
        return data;
    }

    public double getValor() {
        return valor;
    }

    public boolean isPago() {
        return pago;
    }
}
