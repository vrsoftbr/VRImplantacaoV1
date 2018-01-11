package vrimplantacao.vo.notafiscal;

import java.util.List;
import java.util.ArrayList;

public class ConciliacaoBancariaVO {

    public long id = 0;
    public String data = "";
    public int idBanco = 0;
    public String banco = "";
    public String agencia = "";
    public String conta = "";
    public int idSituacaoConciliacaoBancaria = 0;
    public String situacaoConciliacaoBancaria = "";
    public double saldo = 0;
    public double totalDebito = 0;
    public double totalCredito = 0;
    public List<ConciliacaoBancariaLancamentoVO> vLancamento = new ArrayList();    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

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

    public int getIdSituacaoConciliacaoBancaria() {
        return idSituacaoConciliacaoBancaria;
    }

    public void setIdSituacaoConciliacaoBancaria(int idSituacaoConciliacaoBancaria) {
        this.idSituacaoConciliacaoBancaria = idSituacaoConciliacaoBancaria;
    }

    public String getSituacaoConciliacaoBancaria() {
        return situacaoConciliacaoBancaria;
    }

    public void setSituacaoConciliacaoBancaria(String situacaoConciliacaoBancaria) {
        this.situacaoConciliacaoBancaria = situacaoConciliacaoBancaria;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getTotalDebito() {
        return totalDebito;
    }

    public void setTotalDebito(double totalDebito) {
        this.totalDebito = totalDebito;
    }

    public double getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(double totalCredito) {
        this.totalCredito = totalCredito;
    }

    public List<ConciliacaoBancariaLancamentoVO> getvLancamento() {
        return vLancamento;
    }

    public void setvLancamento(List<ConciliacaoBancariaLancamentoVO> vLancamento) {
        this.vLancamento = vLancamento;
    }
}