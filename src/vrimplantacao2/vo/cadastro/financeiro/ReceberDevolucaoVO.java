package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;
import vrimplantacao.utils.Utils;

public class ReceberDevolucaoVO {

    private int idLoja;
    private int idFornecedor;
    private int numeroNota;
    private Date dataemissao;
    private Date datavencimento;
    private Double valor = 0.0;
    private String observacao;
    private int idSituacaoReceberDevolucao = 0;
    private int idTipolocalCobranca = 0;
    private int idTipoDevolucao = 0;
    private boolean lancamentoManual = false;
    private Double valorPagarFornecedor = 0.0;
    private int idNotaSaida;
    private int idBoleto;
    private String justificativa = "";
    private int numeroParcela = 1;
    private boolean exportado = false;

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

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Date getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(Date dataemissao) {
        this.dataemissao = dataemissao;
    }

    public Date getDatavencimento() {
        return datavencimento;
    }

    public void setDatavencimento(Date datavencimento) {
        this.datavencimento = datavencimento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao);
    }

    public int getIdSituacaoReceberDevolucao() {
        return idSituacaoReceberDevolucao;
    }

    public void setIdSituacaoReceberDevolucao(int idSituacaoReceberDevolucao) {
        this.idSituacaoReceberDevolucao = idSituacaoReceberDevolucao;
    }

    public int getIdTipolocalCobranca() {
        return idTipolocalCobranca;
    }

    public void setIdTipolocalCobranca(int idTipolocalCobranca) {
        this.idTipolocalCobranca = idTipolocalCobranca;
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

    public Double getValorPagarFornecedor() {
        return valorPagarFornecedor;
    }

    public void setValorPagarFornecedor(Double valorPagarFornecedor) {
        this.valorPagarFornecedor = valorPagarFornecedor;
    }

    public int getIdNotaSaida() {
        return idNotaSaida;
    }

    public void setIdNotaSaida(int idNotaSaida) {
        this.idNotaSaida = idNotaSaida;
    }

    public int getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = Utils.acertarTexto(justificativa);
    }

    public int getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(int numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public boolean isExportado() {
        return exportado;
    }

    public void setExportado(boolean exportado) {
        this.exportado = exportado;
    }
}
