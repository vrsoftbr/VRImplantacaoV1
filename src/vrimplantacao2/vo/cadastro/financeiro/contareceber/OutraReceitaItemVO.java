package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.util.Date;
import vrimplantacao2.vo.enums.TipoRecebimento;

/**
 *
 * @author Leandro
 */
public class OutraReceitaItemVO {
    
    private int id;// integer NOT NULL DEFAULT nextval('receberoutrasreceitasitem_id_seq'::regclass),
    private long idReceberOutrasReceitas;// id_receberoutrasreceitas bigint NOT NULL,
    private double valor = 0;// numeric(11,2) NOT NULL,
    private double valorDesconto = 0;// numeric(11,2) NOT NULL,
    private double valorJuros = 0;// numeric(11,2) NOT NULL,
    private double valorMulta = 0;// numeric(11,2) NOT NULL,
    private double valorTotal = 0;// numeric(11,2) NOT NULL,
    private Date dataBaixa;// date NOT NULL,
    private Date dataPagamento;// date NOT NULL,
    private String observacao = "";// character varying(500) NOT NULL,
    private int idBanco;//id_banco integer,
    private String agencia;// character varying(10) NOT NULL,
    private String conta;// character varying(10) NOT NULL,
    private TipoRecebimento tipoRecebimento = TipoRecebimento.BOLETO;// id_tiporecebimento integer NOT NULL,
    private long idConciliacaoBancariaLancamento;// id_conciliacaobancarialancamento bigint,
    private long idReceberCheque;//id_recebercheque bigint,
    private int idUsuario = 0;//id_usuario integer NOT NULL DEFAULT 0,
    private int idLoja = 1;//id_loja integer NOT NULL DEFAULT 1, 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIdReceberOutrasReceitas() {
        return idReceberOutrasReceitas;
    }

    public void setIdReceberOutrasReceitas(long idReceberOutrasReceitas) {
        this.idReceberOutrasReceitas = idReceberOutrasReceitas;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
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

    public Date getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(Date dataBaixa) {
        this.dataBaixa = dataBaixa;
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

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
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

    public TipoRecebimento getTipoRecebimento() {
        return tipoRecebimento;
    }

    public void setTipoRecebimento(TipoRecebimento tipoRecebimento) {
        this.tipoRecebimento = tipoRecebimento;
    }

    public long getIdConciliacaoBancariaLancamento() {
        return idConciliacaoBancariaLancamento;
    }

    public void setIdConciliacaoBancariaLancamento(long idConciliacaoBancariaLancamento) {
        this.idConciliacaoBancariaLancamento = idConciliacaoBancariaLancamento;
    }

    public long getIdReceberCheque() {
        return idReceberCheque;
    }

    public void setIdReceberCheque(long idReceberCheque) {
        this.idReceberCheque = idReceberCheque;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }
    
}
