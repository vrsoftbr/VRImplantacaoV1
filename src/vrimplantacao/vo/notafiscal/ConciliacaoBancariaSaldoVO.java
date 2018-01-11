package vrimplantacao.vo.notafiscal;

public class ConciliacaoBancariaSaldoVO {

    public int idBanco = 0;
    public String banco = "";
    public String agencia = "";
    public String conta = "";
    public double saldo = 0;
    public double valorDebito = 0;
    public double valorCredito = 0;
    public double saldoAnterior = 0;

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
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

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
