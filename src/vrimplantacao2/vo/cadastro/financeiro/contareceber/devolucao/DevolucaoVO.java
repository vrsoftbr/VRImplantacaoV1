package vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao;

import java.util.Date;

/**
 *
 * @author Wesley
 */
public class DevolucaoVO {

    private int id;
    private int id_loja;
    private int id_fornecedor;
    private int numeroNota;
    private Date dataEmissao;
    private Date dataVencimento;
    private double valor;
    private String observacao;
    private int id_situacaoreceberdevolucao = 0;
    private int id_tipolocalcobranca = 0;
    private int id_tipodevolucao = 0;
    private boolean lancamentomanual = false;
    private double valorpagarfornecedor = 0;
    private String id_notasaida = null;
    private String id_boleto = null;
    private String justificativa = "";
    private int numeroparcela = 0;
    private boolean exportado = false;
    private Date datahoraalteracao = new Date();
    private Date dataexportacao = new Date();
    private double valorabatimento = 0;
    private double valorliquido = 0;
    private double valorjurosatraso = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(int id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
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

    public int getId_situacaoreceberdevolucao() {
        return id_situacaoreceberdevolucao;
    }

    public void setId_situacaoreceberdevolucao(int id_situacaoreceberdevolucao) {
        this.id_situacaoreceberdevolucao = id_situacaoreceberdevolucao;
    }

    public int getId_tipolocalcobranca() {
        return id_tipolocalcobranca;
    }

    public void setId_tipolocalcobranca(int id_tipolocalcobranca) {
        this.id_tipolocalcobranca = id_tipolocalcobranca;
    }

    public int getId_tipodevolucao() {
        return id_tipodevolucao;
    }

    public void setId_tipodevolucao(int id_tipodevolucao) {
        this.id_tipodevolucao = id_tipodevolucao;
    }

    public boolean isLancamentomanual() {
        return lancamentomanual;
    }

    public void setLancamentomanual(boolean lancamentomanual) {
        this.lancamentomanual = lancamentomanual;
    }

    public double getValorpagarfornecedor() {
        return valorpagarfornecedor;
    }

    public void setValorpagarfornecedor(double valorpagarfornecedor) {
        this.valorpagarfornecedor = valorpagarfornecedor;
    }

    public String getId_notasaida() {
        return id_notasaida;
    }

    public void setId_notasaida(String id_notasaida) {
        this.id_notasaida = id_notasaida;
    }

    public String getId_boleto() {
        return id_boleto;
    }

    public void setId_boleto(String id_boleto) {
        this.id_boleto = id_boleto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public int getNumeroparcela() {
        return numeroparcela;
    }

    public void setNumeroparcela(int numeroparcela) {
        this.numeroparcela = numeroparcela;
    }

    public boolean isExportado() {
        return exportado;
    }

    public void setExportado(boolean exportado) {
        this.exportado = exportado;
    }

    public Date getDatahoraalteracao() {
        return datahoraalteracao;
    }

    public void setDatahoraalteracao(Date datahoraalteracao) {
        this.datahoraalteracao = datahoraalteracao;
    }

    public Date getDataexportacao() {
        return dataexportacao;
    }

    public void setDataexportacao(Date dataexportacao) {
        this.dataexportacao = dataexportacao;
    }

    public double getValorabatimento() {
        return valorabatimento;
    }

    public void setValorabatimento(double valorabatimento) {
        this.valorabatimento = valorabatimento;
    }

    public double getValorliquido() {
        return valorliquido;
    }

    public void setValorliquido(double valorliquido) {
        this.valorliquido = valorliquido;
    }

    public double getValorjurosatraso() {
        return valorjurosatraso;
    }

    public void setValorjurosatraso(double valorjurosatraso) {
        this.valorjurosatraso = valorjurosatraso;
    }

}
