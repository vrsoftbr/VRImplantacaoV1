package vrimplantacao2.vo.cadastro.cliente.cheque;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoAlinea;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoVistaPrazo;

/**
 * Classe que representa a tabela recebercheque no banco de dados.
 * @author Leandro
 */
public class ChequeVO {
    private int id;// integer NOT NULL DEFAULT nextval('recebercheque_id_seq'::regclass),
    private int id_loja = 1;// integer NOT NULL,
    private long cpf = 0;// numeric(14,0) NOT NULL,
    private int numeroCheque = 0;// integer NOT NULL,
    private int id_banco = 804;// integer NOT NULL,
    private String agencia = "";// character varying(10) NOT NULL,
    private String conta = "";// character varying(10) NOT NULL,
    private Date data = new Date();// date NOT NULL,
    private int id_plano;// integer,
    private int numeroCupom = 0;// integer NOT NULL,
    private int ecf = 0;// integer NOT NULL,
    private double valor = 0;// numeric(12,2) NOT NULL,
    private Date dataDeposito = new Date();// date NOT NULL,
    private boolean lancamentoManual = false;// boolean NOT NULL,
    private String rg = "ISENTO";// character varying(20) NOT NULL,
    private String telefone = "0000000000";// character varying(14) NOT NULL,
    private String nome = "";// character varying(40) NOT NULL,
    private String observacao = "";// character varying(500),
    private SituacaoCheque situacaoCheque = SituacaoCheque.ABERTO;// id_situacaorecebercheque integer NOT NULL,
    private int tipoLocalCobranca = 0;// id_tipoLocalCobranca integer NOT NULL,
    private String cmc7 = "";// character varying(34) NOT NULL,
    private Date dataDevolucao;// date,
    private TipoAlinea tipoAlinea = TipoAlinea.A0_VALIDO; //id_tipoalinea integer NOT NULL,
    private TipoInscricao tipoInscricao = TipoInscricao.JURIDICA;// id_tipoinscricao integer NOT NULL,
    private Date dataEnvioCobranca;// date
    private double valorPagarFornecedor = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private long id_boleto = 0;// bigint,
    private String operadorClienteBloqueado;// character varying(40),
    private String operadorExcedeLimite;// character varying(40),
    private String operadorProblemaCheque;// character varying(40),
    private String operadorChequeBloqueado;// character varying(40),
    private double valorJuros = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private TipoVistaPrazo tipoVistaPrazo = TipoVistaPrazo.A_VISTA;// id_tipovistaprazo integer NOT NULL DEFAULT 0,
    private String justificativa = "";// character varying(50) NOT NULL DEFAULT ''::character varying,
    private double valorAcrescimo = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double valorInicial = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private Timestamp dataHoraAlteracao = new Timestamp(new Date().getTime());// timestamp without time zone NOT NULL DEFAULT now(),
    private String operadorClienteNaoCadastrado;// character varying(40),

    public void setId(int id) {
        this.id = id;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setCpf(long cpf) {
        if (cpf > 99999999999999L) {
            cpf = this.id;
        }
        this.cpf = cpf;
    }

    public void setNumeroCheque(int numeroCheque) {
        this.numeroCheque = numeroCheque;
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

    public void setData(Date data) {
        this.data = data != null ? data : new Date();
    }

    public void setId_plano(int id_plano) {
        this.id_plano = id_plano;
    }

    public void setNumeroCupom(int numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setDataDeposito(Date dataDeposito) {
        this.dataDeposito = dataDeposito;
    }

    public void setLancamentoManual(boolean lancamentoManual) {
        this.lancamentoManual = lancamentoManual;
    }

    public void setRg(String rg) {
        this.rg = Utils.formataNumero(rg, 20, "ISENTO");
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.formataNumero(telefone, 14, "0000000000");
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 40, "SEM NOME");
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 500);
    }

    public void setSituacaoCheque(SituacaoCheque situacaoCheque) {
        this.situacaoCheque = situacaoCheque != null ? situacaoCheque : SituacaoCheque.ABERTO;
    }

    public void setTipoLocalCobranca(int tipoLocalCobranca) {
        this.tipoLocalCobranca = tipoLocalCobranca;
    }

    public void setCmc7(String cmc7) {
        this.cmc7 = Utils.formataNumero(cmc7, 34);
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public void setTipoAlinea(TipoAlinea tipoAlinea) {
        this.tipoAlinea = tipoAlinea != null ? tipoAlinea : TipoAlinea.A0_VALIDO;
    }

    public void setTipoInscricao(TipoInscricao tipoInscricao) {
        this.tipoInscricao = tipoInscricao != null ? tipoInscricao : TipoInscricao.JURIDICA;
    }

    public void setDataEnvioCobranca(Date dataEnvioCobranca) {
        this.dataEnvioCobranca = dataEnvioCobranca;
    }

    public void setValorPagarFornecedor(double valorPagarFornecedor) {
        this.valorPagarFornecedor = valorPagarFornecedor;
    }

    public void setId_boleto(long id_boleto) {
        this.id_boleto = id_boleto;
    }

    public void setOperadorClienteBloqueado(String operadorClienteBloqueado) {
        this.operadorClienteBloqueado = Utils.acertarTexto(operadorClienteBloqueado, 40);
    }

    public void setOperadorExcedeLimite(String operadorExcedeLimite) {
        this.operadorExcedeLimite = Utils.acertarTexto(operadorExcedeLimite, 40);
    }

    public void setOperadorProblemaCheque(String operadorProblemaCheque) {
        this.operadorProblemaCheque = Utils.acertarTexto(operadorProblemaCheque, 40);
    }

    public void setOperadorChequeBloqueado(String operadorChequeBloqueado) {
        this.operadorChequeBloqueado = Utils.acertarTexto(operadorChequeBloqueado, 40);
    }

    public void setValorJuros(double valorJuros) {
        this.valorJuros = valorJuros;
    }

    public void setTipoVistaPrazo(TipoVistaPrazo tipoVistaPrazo) {
        this.tipoVistaPrazo = tipoVistaPrazo;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public void setValorAcrescimo(double valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public void setValorInicial(double valorInicial) {
        this.valorInicial = valorInicial;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public void setOperadorClienteNaoCadastrado(String operadorClienteNaoCadastrado) {
        this.operadorClienteNaoCadastrado = Utils.acertarTexto(operadorClienteNaoCadastrado, 40);
    }

    public int getId() {
        return id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public long getCpf() {
        return cpf;
    }

    public int getNumeroCheque() {
        return numeroCheque;
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

    public Date getData() {
        return data;
    }

    public int getId_plano() {
        return id_plano;
    }

    public int getNumeroCupom() {
        return numeroCupom;
    }

    public int getEcf() {
        return ecf;
    }

    public double getValor() {
        return valor;
    }

    public Date getDataDeposito() {
        return dataDeposito;
    }

    public boolean isLancamentoManual() {
        return lancamentoManual;
    }

    public String getRg() {
        return rg;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getNome() {
        return nome;
    }

    public String getObservacao() {
        return observacao;
    }

    public SituacaoCheque getSituacaoCheque() {
        return situacaoCheque;
    }

    public int getTipoLocalCobranca() {
        return tipoLocalCobranca;
    }

    public String getCmc7() {
        return cmc7;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public TipoAlinea getTipoAlinea() {
        return tipoAlinea;
    }

    public TipoInscricao getTipoInscricao() {
        return tipoInscricao;
    }

    public Date getDataEnvioCobranca() {
        return dataEnvioCobranca;
    }

    public double getValorPagarFornecedor() {
        return valorPagarFornecedor;
    }

    public long getId_boleto() {
        return id_boleto;
    }

    public String getOperadorClienteBloqueado() {
        return operadorClienteBloqueado;
    }

    public String getOperadorExcedeLimite() {
        return operadorExcedeLimite;
    }

    public String getOperadorProblemaCheque() {
        return operadorProblemaCheque;
    }

    public String getOperadorChequeBloqueado() {
        return operadorChequeBloqueado;
    }

    public double getValorJuros() {
        return valorJuros;
    }

    public TipoVistaPrazo getTipoVistaPrazo() {
        return tipoVistaPrazo;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public double getValorAcrescimo() {
        return valorAcrescimo;
    }

    public double getValorInicial() {
        return valorInicial;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public String getOperadorClienteNaoCadastrado() {
        return operadorClienteNaoCadastrado;
    }
}
