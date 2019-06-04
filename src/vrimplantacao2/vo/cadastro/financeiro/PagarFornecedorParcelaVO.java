package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;
import vrimplantacao.utils.Utils;

/**
 * Representa um registro da tabela public.pagarfornecedorparcela.
 * @author Leandro
 */
public class PagarFornecedorParcelaVO {
    
    
    private int id;// integer NOT NULL DEFAULT nextval('pagarfornecedorparcela_id_seq'::regclass),
    private long id_pagarfornecedor;// bigint NOT NULL,
    private int numeroparcela = 1;// integer NOT NULL,
    private Date datavencimento;// date NOT NULL,
    private Date datapagamento;// date,
    private double valor;// numeric(11,2) NOT NULL,
    private String observacao;// character varying(280),
    private SituacaoPagarFornecedorParcela situacaopagarfornecedorparcela = SituacaoPagarFornecedorParcela.ABERTO;//id_situacaopagarfornecedorparcela integer NOT NULL,
    private int id_tipopagamento = 0;// integer NOT NULL,
    private Date datapagamentocontabil;// date,
    private int id_banco = -1;// integer,
    private String agencia = "";// character varying(10) NOT NULL,
    private String conta = "";// character varying(10) NOT NULL,
    private int numerocheque = 0;// integer NOT NULL,
    private boolean conferido = false;// boolean NOT NULL,
    private double valoracrescimo = 0;// numeric(11,2) NOT NULL,
    private int id_contacontabilfinanceiro = -1;// integer,
    private long id_conciliacaobancarialancamento = -1;// bigint,
    private boolean exportado = false;// boolean NOT NULL DEFAULT false,
    private Date datahoraalteracao;// timestamp without time zone NOT NULL DEFAULT now(),

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getId_pagarfornecedor() {
        return id_pagarfornecedor;
    }

    public void setId_pagarfornecedor(long id_pagarfornecedor) {
        this.id_pagarfornecedor = id_pagarfornecedor;
    }

    public int getNumeroparcela() {
        return numeroparcela;
    }

    public void setNumeroparcela(int numeroparcela) {
        this.numeroparcela = numeroparcela;
    }

    public Date getDatavencimento() {
        return datavencimento;
    }

    public void setDatavencimento(Date datavencimento) {
        this.datavencimento = datavencimento;
    }

    public Date getDatapagamento() {
        return datapagamento;
    }

    public void setDatapagamento(Date datapagamento) {
        this.datapagamento = datapagamento;
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
        this.observacao = Utils.acertarTexto(observacao, 280);
    }

    public SituacaoPagarFornecedorParcela getSituacaopagarfornecedorparcela() {
        return situacaopagarfornecedorparcela;
    }

    public void setSituacaopagarfornecedorparcela(SituacaoPagarFornecedorParcela situacaopagarfornecedorparcela) {
        this.situacaopagarfornecedorparcela = situacaopagarfornecedorparcela == null ? SituacaoPagarFornecedorParcela.ABERTO : situacaopagarfornecedorparcela;
    }

    public int getId_tipopagamento() {
        return id_tipopagamento;
    }

    public void setId_tipopagamento(int id_tipopagamento) {
        this.id_tipopagamento = id_tipopagamento;
    }

    public Date getDatapagamentocontabil() {
        return datapagamentocontabil;
    }

    public void setDatapagamentocontabil(Date datapagamentocontabil) {
        this.datapagamentocontabil = datapagamentocontabil;
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
        this.agencia = Utils.acertarTexto(agencia, 10);
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = Utils.acertarTexto(conta, 10);
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

    public double getValoracrescimo() {
        return valoracrescimo;
    }

    public void setValoracrescimo(double valoracrescimo) {
        this.valoracrescimo = valoracrescimo;
    }

    public int getId_contacontabilfinanceiro() {
        return id_contacontabilfinanceiro;
    }

    public void setId_contacontabilfinanceiro(int id_contacontabilfinanceiro) {
        this.id_contacontabilfinanceiro = id_contacontabilfinanceiro;
    }

    public long getId_conciliacaobancarialancamento() {
        return id_conciliacaobancarialancamento;
    }

    public void setId_conciliacaobancarialancamento(long id_conciliacaobancarialancamento) {
        this.id_conciliacaobancarialancamento = id_conciliacaobancarialancamento;
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
    
    
    
}
