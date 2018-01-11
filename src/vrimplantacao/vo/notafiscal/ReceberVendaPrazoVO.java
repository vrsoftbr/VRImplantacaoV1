package vrimplantacao.vo.notafiscal;

import java.util.List;
import java.util.ArrayList;

public class ReceberVendaPrazoVO {

    public long id = 0;
    public int idLoja = 0;
    public String loja = "";
    public int idClienteEventual = 0;
    public String clienteEventual = "";
    public String dataEmissao = "";
    public String dataVencimento = "";
    public double valor = 0;
    public double valorLiquido = 0;
    public String observacao = "";
    public int idSituacaoReceberVendaPrazo = 0;
    public String situacaoReceberVendaPrazo = "";
    public int idTipoLocalCobranca = 0;
    public String tipoLocalCobranca = "";
    public int numeroNota = 0;
    public double impostoRenda = 0;
    public double pis = 0;
    public double cofins = 0;
    public double csll = 0;
    public int idTipoSaida = 0;
    public String tipoSaida = "";
    public boolean lancamentoManual = true;
    public List<ReceberVendaPrazoItemVO> vItem = new ArrayList();
    public List<ReceberVendaPrazoItemVO> vItemExclusao = new ArrayList();
    public double valorRecebido = 0;
    public double valorRecebidoLiquido = 0;
    public double valorDesconto = 0;
    public double valorJuros = 0;
    public double valorJurosItem = 0;
    public double valorMulta = 0;
    public double valorTotal = 0;
    public double valorRestante = 0;
    public long idNotaSaida = 0;
    public long idBoleto = -1;
    public boolean exportado = false;
    public int numeroParcela = 0;

    public double getValorRecebidoLiquido() {
        return valorRecebidoLiquido;
    }

    public void setValorRecebidoLiquido(double valorRecebidoLiquido) {
        this.valorRecebidoLiquido = valorRecebidoLiquido;
    }

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

    public int getIdClienteEventual() {
        return idClienteEventual;
    }

    public void setIdClienteEventual(int idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public String getClienteEventual() {
        return clienteEventual;
    }

    public void setClienteEventual(String clienteEventual) {
        this.clienteEventual = clienteEventual;
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

    public double getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(double valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getIdSituacaoReceberVendaPrazo() {
        return idSituacaoReceberVendaPrazo;
    }

    public void setIdSituacaoReceberVendaPrazo(int idSituacaoReceberVendaPrazo) {
        this.idSituacaoReceberVendaPrazo = idSituacaoReceberVendaPrazo;
    }

    public String getSituacaoReceberVendaPrazo() {
        return situacaoReceberVendaPrazo;
    }

    public void setSituacaoReceberVendaPrazo(String situacaoReceberVendaPrazo) {
        this.situacaoReceberVendaPrazo = situacaoReceberVendaPrazo;
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

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public double getImpostoRenda() {
        return impostoRenda;
    }

    public void setImpostoRenda(double impostoRenda) {
        this.impostoRenda = impostoRenda;
    }

    public double getPis() {
        return pis;
    }

    public void setPis(double pis) {
        this.pis = pis;
    }

    public double getCofins() {
        return cofins;
    }

    public void setCofins(double cofins) {
        this.cofins = cofins;
    }

    public double getCsll() {
        return csll;
    }

    public void setCsll(double csll) {
        this.csll = csll;
    }

    public int getIdTipoSaida() {
        return idTipoSaida;
    }

    public void setIdTipoSaida(int idTipoSaida) {
        this.idTipoSaida = idTipoSaida;
    }

    public String getTipoSaida() {
        return tipoSaida;
    }

    public void setTipoSaida(String tipoSaida) {
        this.tipoSaida = tipoSaida;
    }

    public boolean isLancamentoManual() {
        return lancamentoManual;
    }

    public void setLancamentoManual(boolean lancamentoManual) {
        this.lancamentoManual = lancamentoManual;
    }

    public List<ReceberVendaPrazoItemVO> getvItem() {
        return vItem;
    }

    public void setvItem(List<ReceberVendaPrazoItemVO> vItem) {
        this.vItem = vItem;
    }

    public List<ReceberVendaPrazoItemVO> getvItemExclusao() {
        return vItemExclusao;
    }

    public void setvItemExclusao(List<ReceberVendaPrazoItemVO> vItemExclusao) {
        this.vItemExclusao = vItemExclusao;
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

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getValorRestante() {
        return valorRestante;
    }

    public void setValorRestante(double valorRestante) {
        this.valorRestante = valorRestante;
    }

    /**
     * @return the loja
     */
    public String getLoja() {
        return loja;
    }

    /**
     * @param loja the loja to set
     */
    public void setLoja(String loja) {
        this.loja = loja;
    }
}
