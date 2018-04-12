package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
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
    //private double valorTotal = 0 numeric(11,2) NOT NULL,
    private Date dataBaixa = new Date();// date NOT NULL,
    private Date dataPagamento = new Date();// date NOT NULL,
    private String observacao = "IMPORTADO VR";// character varying(500) NOT NULL,
    private int idBanco;//id_banco integer,
    private String agencia;// character varying(10) NOT NULL,
    private String conta;// character varying(10) NOT NULL,
    private TipoRecebimento tipoRecebimento = TipoRecebimento.CARTEIRA;// id_tiporecebimento integer NOT NULL,
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
        this.valor = MathUtils.round(valor, 2, 999999.99F);
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = MathUtils.round(valorDesconto, 2, 999999.99F);
    }

    public double getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(double valorJuros) {
        this.valorJuros = MathUtils.round(valorJuros, 2, 999999.99F);
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = MathUtils.round(valorMulta, 2, 999999.99F);
    }

    public double getValorTotal() {
        return MathUtils.round(getValor() - getValorDesconto() + getValorJuros() + getValorMulta(), 2, 999999.99F);
    }

    public Date getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(Date dataBaixa) {
        this.dataBaixa = dataBaixa == null ? new Date() : dataBaixa;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento == null ? new Date() : dataPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto("IMPORTADOR VR " + observacao, 500);
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
        this.agencia = agencia == null ? "" : agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta == null ? "" : conta;
    }

    public TipoRecebimento getTipoRecebimento() {
        return tipoRecebimento;
    }

    public void setTipoRecebimento(TipoRecebimento tipoRecebimento) {
        this.tipoRecebimento = tipoRecebimento == null ? TipoRecebimento.CARTEIRA : tipoRecebimento;
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
