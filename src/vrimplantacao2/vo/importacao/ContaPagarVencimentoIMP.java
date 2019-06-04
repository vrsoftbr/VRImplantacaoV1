package vrimplantacao2.vo.importacao;

import java.util.Date;

/**
 *
 * @author Leandro
 */
public class ContaPagarVencimentoIMP {
    private String id;
    private ContaPagarIMP contaPagar;
    private Date vencimento;
    private double valor = 0;
    //Somente pagarfornecedor
    private int numeroParcela = 1;
    private Date dataPagamento;
    private String observacao;
    private boolean pago = false;
    private Integer idTipoPagamentoVR = null;
    private int id_banco = -1;
    private String agencia = "";
    private String conta = "";
    private int numerocheque = 0;
    private boolean conferido = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContaPagarIMP getContaPagar() {
        return contaPagar;
    }

    public void setContaPagar(ContaPagarIMP contaPagar) {
        this.contaPagar = contaPagar;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(int numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public Integer getIdTipoPagamentoVR() {
        return idTipoPagamentoVR;
    }

    public void setIdTipoPagamentoVR(Integer idTipoPagamentoVR) {
        this.idTipoPagamentoVR = idTipoPagamentoVR;
    }

    public int getId_banco() {
        return id_banco;
    }

    public void setId_banco(int id_banco) {
        this.id_banco = id_banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public int getNumerocheque() {
        return numerocheque;
    }

    public void setNumerocheque(int numerocheque) {
        this.numerocheque = numerocheque;
    }

    public boolean isConferido() {
        return conferido;
    }

    public void setConferido(boolean conferido) {
        this.conferido = conferido;
    }
    
}
