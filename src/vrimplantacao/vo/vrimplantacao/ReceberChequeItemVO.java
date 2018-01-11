package vrimplantacao.vo.vrimplantacao;

import java.util.Date;

/**
 * Classe que representa a baixa de um cheque no banco de dados.
 * @author Leandro
 */
public class ReceberChequeItemVO {
    private int id = -1;
    private long id_recebercheque = -1;
    private double valor = 0;
    private double valordesconto = 0;
    private double valorjuros = 0;
    private double valormulta = 0;
    private Date databaixa = new Date();
    private Date datapagamento = new Date();
    private String observacao = "IMPORTADO VR";
    private int id_banco = 804;
    private String agencia = "1";
    private String conta = "1";
    private int id_tiporecebimento = 0;
    private long id_pagarfornecedorparcela = -1;
    private long id_conciliacaobancarialancamento = -1;
    private long id_receberchequepagamento = -1;
    private long id_recebercaixa = -1;
    private long id_usuario = 0;
    private int id_loja = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getId_recebercheque() {
        return id_recebercheque;
    }

    public void setId_recebercheque(long id_recebercheque) {
        this.id_recebercheque = id_recebercheque;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValordesconto() {
        return valordesconto;
    }

    public void setValordesconto(double valordesconto) {
        this.valordesconto = valordesconto;
    }

    public double getValorjuros() {
        return valorjuros;
    }

    public void setValorjuros(double valorjuros) {
        this.valorjuros = valorjuros;
    }

    public double getValormulta() {
        return valormulta;
    }

    public void setValormulta(double valormulta) {
        this.valormulta = valormulta;
    }

    public double getValortotal() {
        return getValor() + getValorjuros() + getValormulta() - getValordesconto();
    }

    public Date getDatabaixa() {
        return databaixa;
    }

    public void setDatabaixa(Date databaixa) {
        this.databaixa = databaixa;
    }

    public Date getDatapagamento() {
        return datapagamento;
    }

    public void setDatapagamento(Date datapagamento) {
        this.datapagamento = datapagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public int getId_tiporecebimento() {
        return id_tiporecebimento;
    }

    public void setId_tiporecebimento(int id_tiporecebimento) {
        this.id_tiporecebimento = id_tiporecebimento;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public long getId_pagarfornecedorparcela() {
        return id_pagarfornecedorparcela;
    }

    public void setId_pagarfornecedorparcela(long id_pagarfornecedorparcela) {
        this.id_pagarfornecedorparcela = id_pagarfornecedorparcela;
    }

    public long getId_conciliacaobancarialancamento() {
        return id_conciliacaobancarialancamento;
    }

    public void setId_conciliacaobancarialancamento(long id_conciliacaobancarialancamento) {
        this.id_conciliacaobancarialancamento = id_conciliacaobancarialancamento;
    }

    public long getId_receberchequepagamento() {
        return id_receberchequepagamento;
    }

    public void setId_receberchequepagamento(long id_receberchequepagamento) {
        this.id_receberchequepagamento = id_receberchequepagamento;
    }

    public long getId_recebercaixa() {
        return id_recebercaixa;
    }

    public void setId_recebercaixa(long id_recebercaixa) {
        this.id_recebercaixa = id_recebercaixa;
    }

    public long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(long id_usuario) {
        this.id_usuario = id_usuario;
    }
    
    

    //Assistentes da integração
    public String impSistemaId;
    public String impLojaId;
    public String impId;
    
    public String getChave() {
        return impSistemaId + "-" + impId + "-" + impId;
    }
}
