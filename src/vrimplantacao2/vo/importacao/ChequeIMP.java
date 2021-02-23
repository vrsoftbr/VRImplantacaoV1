package vrimplantacao2.vo.importacao;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoVistaPrazo;

/**
 * Classe utilizada para a importação de cheques no VR.
 * @author Leandro
 */
public class ChequeIMP {
    private String id;
    private String cpf;
    private String numeroCheque;
    private int banco;
    private String agencia;
    private String conta;
    private Date date = new Date();
    private Date dataDeposito = new Date();
    private String numeroCupom = "";
    private String ecf = "0";
    private double valor = 0;
    private String rg;
    private String telefone;
    private String nome;
    private String observacao;
    private SituacaoCheque situacaoCheque  = SituacaoCheque.ABERTO;
    private String cmc7;    
    private int alinea = 0;
    private double valorJuros = 0;
    private double valorAcrescimo = 0;
    private int idLocalCobranca = 0;
    private Timestamp dataHoraAlteracao = new Timestamp(new Date().getTime());
    private TipoVistaPrazo vistaPrazo = TipoVistaPrazo.A_VISTA;
    private Date dataDevolucao = null;
    public void setId(String id) {
        this.id = id;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public void setBanco(int banco) {
        this.banco = banco;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setNumeroCupom(String numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public void setEcf(String ecf) {
        this.ecf = ecf;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setSituacaoCheque(SituacaoCheque situacaoCheque) {
        this.situacaoCheque = situacaoCheque != null ? situacaoCheque : SituacaoCheque.ABERTO;
    }

    public void setCmc7(String cmc7) {
        this.cmc7 = cmc7;
    }

    public void setAlinea(int alinea) {
        this.alinea = alinea;
    }

    public void setValorJuros(double valorJuros) {
        this.valorJuros = valorJuros;
    }

    public void setValorAcrescimo(double valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao != null ? dataHoraAlteracao : new Timestamp(new Date().getTime());
    }

    public void setVistaPrazo(TipoVistaPrazo vistaPrazo) {
        this.vistaPrazo = vistaPrazo != null ? vistaPrazo : TipoVistaPrazo.A_VISTA;
    }

    public String getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNumeroCheque() {
        return numeroCheque;
    }

    public int getBanco() {
        return banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getConta() {
        return conta;
    }

    public Date getDate() {
        return date;
    }

    public String getNumeroCupom() {
        return numeroCupom;
    }

    public String getEcf() {
        return ecf;
    }

    public double getValor() {
        return valor;
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

    public String getCmc7() {
        return cmc7;
    }

    public int getAlinea() {
        return alinea;
    }

    public double getValorJuros() {
        return valorJuros;
    }

    public double getValorAcrescimo() {
        return valorAcrescimo;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public TipoVistaPrazo getVistaPrazo() {
        return vistaPrazo;
    }

    public Date getDataDeposito() {
        return dataDeposito;
    }

    public void setDataDeposito(Date dataDeposito) {
        this.dataDeposito = dataDeposito;
    }

    public int getIdLocalCobranca() {
        return idLocalCobranca;
    }

    public void setIdLocalCobranca(int idLocalCobranca) {
        this.idLocalCobranca = idLocalCobranca;
    }
    
    /**
     * @return the dataDevolucao
     */
    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    /**
     * @param dataDevolucao the dataDevolucao to set
     */
    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }
    
}
