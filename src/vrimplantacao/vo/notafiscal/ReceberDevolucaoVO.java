package vrimplantacao.vo.notafiscal;

import java.util.List;
import java.util.ArrayList;

public class ReceberDevolucaoVO {

    public long id = 0;
    public int idLoja = 0;
    public String loja = "";
    public int idFornecedor = 0;
    public String fornecedor = "";
    public int numeroNota = 0;
    public String dataEmissao = "";
    public String dataVencimento = "";
    public double valor = 0;
    public String observacao = "";
    public int idSituacaoReceberDevolucao = 0;
    public String situacaoReceberDevolucao = "";
    public int idTipoLocalCobranca = 0;
    public String tipoLocalCobranca = "";
    //public boolean tipoRecebimento = false;
    public int idTipoDevolucao = 0;
    public boolean lancamentoManual = false;
    public List<ReceberDevolucaoItemVO> vItem = new ArrayList();
    public List<ReceberDevolucaoItemVO> vItemExclusao = new ArrayList();
    public double valorTotal = 0;
    public double valorRecebido = 0;
    public double valorRecebidoLiquido = 0;
    public double valorDesconto = 0;
    public double valorJuros = 0;
    public double valorMulta = 0;
    public double valorRestante = 0;
    public double valorPagarFornecedor = 0;
    public long idNotaSaida = 0;
    public long idBoleto = -1;
    public boolean exportado = false;
    public int numeroParcela = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getIdSituacaoReceberDevolucao() {
        return idSituacaoReceberDevolucao;
    }

    public void setIdSituacaoReceberDevolucao(int idSituacaoReceberDevolucao) {
        this.idSituacaoReceberDevolucao = idSituacaoReceberDevolucao;
    }

    public String getSituacaoReceberDevolucao() {
        return situacaoReceberDevolucao;
    }

    public void setSituacaoReceberDevolucao(String situacaoReceberDevolucao) {
        this.situacaoReceberDevolucao = situacaoReceberDevolucao;
    }

    public int getIdTipoLocalCobranca() {
        return idTipoLocalCobranca;
    }

    public void setIdTipoLocalCobranca(int idTipoLocalCobranca) {
        this.idTipoLocalCobranca = idTipoLocalCobranca;
    }

    public String getTipoLocalCobranca() {
        return tipoLocalCobranca;
    }

    public void setTipoLocalCobranca(String tipoLocalCobranca) {
        this.tipoLocalCobranca = tipoLocalCobranca;
    }

    public int getIdTipoDevolucao() {
        return idTipoDevolucao;
    }

    public void setIdTipoDevolucao(int idTipoDevolucao) {
        this.idTipoDevolucao = idTipoDevolucao;
    }

    public boolean isLancamentoManual() {
        return lancamentoManual;
    }

    public void setLancamentoManual(boolean lancamentoManual) {
        this.lancamentoManual = lancamentoManual;
    }

    public List<ReceberDevolucaoItemVO> getvItem() {
        return vItem;
    }

    public void setvItem(List<ReceberDevolucaoItemVO> vItem) {
        this.vItem = vItem;
    }

    public List<ReceberDevolucaoItemVO> getvItemExclusao() {
        return vItemExclusao;
    }

    public void setvItemExclusao(List<ReceberDevolucaoItemVO> vItemExclusao) {
        this.vItemExclusao = vItemExclusao;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(double valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(double valorJuros) {
        this.valorJuros = valorJuros;
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = valorMulta;
    }

    public double getValorRestante() {
        return valorRestante;
    }

    public void setValorRestante(double valorRestante) {
        this.valorRestante = valorRestante;
    }

    public double getValorPagarFornecedor() {
        return valorPagarFornecedor;
    }

    public void setValorPagarFornecedor(double valorPagarFornecedor) {
        this.valorPagarFornecedor = valorPagarFornecedor;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public long getIdNotaSaida() {
        return idNotaSaida;
    }

    public void setIdNotaSaida(long idNotaSaida) {
        this.idNotaSaida = idNotaSaida;
    }
}
