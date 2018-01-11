package vrimplantacao2.vo.cadastro.cliente.rotativo;

import java.util.Date;
import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoItemVO {
    private int id;// integer NOT NULL DEFAULT nextval('recebercreditorotativoitem_id_seq'::regclass),
    private int id_receberCreditoRotativo;// bigint NOT NULL,
    private double valor = 0F;// numeric(11,2) NOT NULL,
    private double valorDesconto = 0F;// numeric(11,2) NOT NULL,
    private double valorMulta = 0F;// numeric(11,2) NOT NULL,
    private double valorTotal = 0F;// numeric(11,2) NOT NULL,
    private Date databaixa = new Date();// date NOT NULL,
    private Date dataPagamento = new Date();// date NOT NULL,
    private String observacao = "";// character varying(500) NOT NULL,
    private int id_banco = 999;// integer,
    private String agencia = "";// character varying(10) NOT NULL,
    private String conta = "";// character varying(10) NOT NULL,
    private int id_tipoRecebimento = 0;// integer NOT NULL,
    private int id_usuario = 0;// integer NOT NULL,
    private int id_loja = 1;// integer NOT NULL,
    private long id_receberCheque = 0;// bigint,
    private long id_receberCaixa = 0;// bigint,
    private long id_conciliacaoBancariaLancamento = 0;// bigint,

    public void setId(int id) {
        this.id = id;
    }

    public void setId_receberCreditoRotativo(int id_receberCreditoRotativo) {
        this.id_receberCreditoRotativo = id_receberCreditoRotativo;
    }

    public void setValor(double valor) {
        this.valor = valor < 0 ? 0 : valor;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto < 0 ? 0 : valorDesconto;
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = valorMulta < 0 ? 0 : valorMulta;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal < 0 ? 0 : valorTotal;
    }

    public void setDatabaixa(Date databaixa) {
        this.databaixa = databaixa;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 500);
    }

    public void setId_banco(int id_banco) {
        this.id_banco = id_banco;
    }

    public void setAgencia(String agencia) {
        this.agencia = Utils.acertarTexto(agencia, 10);
    }

    public void setConta(String conta) {
        this.conta = Utils.acertarTexto(conta, 10);
    }

    public void setId_tipoRecebimento(int id_tipoRecebimento) {
        this.id_tipoRecebimento = id_tipoRecebimento;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setId_receberCheque(long id_receberCheque) {
        this.id_receberCheque = id_receberCheque;
    }

    public void setId_receberCaixa(long id_receberCaixa) {
        this.id_receberCaixa = id_receberCaixa;
    }

    public void setId_conciliacaoBancariaLancamento(long id_conciliacaoBancariaLancamento) {
        this.id_conciliacaoBancariaLancamento = id_conciliacaoBancariaLancamento;
    }

    public int getId() {
        return id;
    }

    public int getId_receberCreditoRotativo() {
        return id_receberCreditoRotativo;
    }

    public double getValor() {
        return valor;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public double getValorTotal() {
        return getValor() + getValorMulta() - getValorDesconto();
    }

    public Date getDatabaixa() {
        return databaixa;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public int getId_banco() {
        return id_banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getConta() {
        return conta;
    }

    public int getId_tipoRecebimento() {
        return id_tipoRecebimento;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public int getId_loja() {
        return id_loja;
    }

    public long getId_receberCheque() {
        return id_receberCheque;
    }

    public long getId_receberCaixa() {
        return id_receberCaixa;
    }

    public long getId_conciliacaoBancariaLancamento() {
        return id_conciliacaoBancariaLancamento;
    }
}
