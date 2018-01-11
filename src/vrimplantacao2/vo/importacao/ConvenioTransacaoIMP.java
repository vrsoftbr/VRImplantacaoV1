package vrimplantacao2.vo.importacao;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao2.vo.cadastro.convenio.transacao.SituacaoTransacaoConveniado;

/**
 *
 * @author Leandro
 */
public class ConvenioTransacaoIMP {
    private String id;
    private String idConveniado;
    private String ecf;
    private String numeroCupom;
    private Timestamp dataHora;    
    private double valor = 0;
    private SituacaoTransacaoConveniado situacaoTransacaoConveniado = SituacaoTransacaoConveniado.OK;        
    private Date dataMovimento;
    private boolean finalizado = false;
    private String observacao = "";

    public void setId(String id) {
        this.id = id;
    }

    public void setIdConveniado(String idConveniado) {
        this.idConveniado = idConveniado;
    }

    public void setEcf(String ecf) {
        this.ecf = ecf;
    }

    public void setNumeroCupom(String numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setSituacaoTransacaoConveniado(SituacaoTransacaoConveniado situacaoTransacaoConveniado) {
        this.situacaoTransacaoConveniado = situacaoTransacaoConveniado;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getId() {
        return id;
    }

    public String getIdConveniado() {
        return idConveniado;
    }

    public String getEcf() {
        return ecf;
    }

    public String getNumeroCupom() {
        return numeroCupom;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public double getValor() {
        return valor;
    }

    public SituacaoTransacaoConveniado getSituacaoTransacaoConveniado() {
        return situacaoTransacaoConveniado;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public String getObservacao() {
        return observacao;
    }
    
    
}
