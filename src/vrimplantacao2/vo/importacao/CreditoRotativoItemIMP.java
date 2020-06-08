package vrimplantacao2.vo.importacao;

import java.util.Date;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoItemIMP {
    private CreditoRotativoIMP creditoRotativo;
    private String id;
    private double valor = 0D;
    private double desconto = 0D;
    private double multa = 0D;
    
    private int id_banco = 804;
    private String agencia;
    private String conta;
    private int id_tiporecebimento = 0;
    
    private Date dataPagamento;
    private String observacao;

    public CreditoRotativoIMP getCreditoRotativo() {
        return creditoRotativo;
    }

    public String getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getMulta() {
        return multa;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public String getObservacao() {
        return observacao;
    }
    
    public double getTotal() {
        return valor + multa - desconto;
    }

    public void setCreditoRotativo(CreditoRotativoIMP creditoRotativo) {
        this.creditoRotativo = creditoRotativo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

    public int getId_tiporecebimento() {
        return id_tiporecebimento;
    }

    public void setId_tiporecebimento(int id_tiporecebimento) {
        this.id_tiporecebimento = id_tiporecebimento;
    }
    
}
