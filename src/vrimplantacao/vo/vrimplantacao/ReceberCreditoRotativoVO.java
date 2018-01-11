package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;
import java.sql.Timestamp;
import vrimplantacao.utils.Utils;

public class ReceberCreditoRotativoVO {

    public int id_loja = 0; //integer NOT NULL,
    public String dataemissao; //date NOT NULL,
    public int numerocupom = 0;// integer NOT NULL,
    public int ecf = 0;// integer,
    public double valor = 0;// numeric(11,2) NOT NULL,
    public boolean lancamentomanual = true;// boolean NOT NULL,
    public String observacao = "";// character varying(500) NOT NULL,
    public int id_situacaorecebercreditorotativo = 0;//integer NOT NULL,
    public int id_clientepreferencial = 0;//integer NOT NULL,
    public String datavencimento;// date NOT NULL,
    public int matricula = 500001;// integer,
    public int parcela = 1;// integer NOT NULL,
    public double valorjuros = 0;//numeric(11,2) NOT NULL DEFAULT 0,
    public int id_boleto;// bigint,
    public int id_tipolocalcobranca = 0;// integer NOT NULL DEFAULT 0,
    public double valormulta = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    public String justificativa = "";//character varying(50) NOT NULL DEFAULT ''::character varying,
    public boolean exportado = false;// boolean NOT NULL DEFAULT false,
    public Timestamp datahoraalteracao; //timestamp without time zone NOT NULL DEFAULT now(),   
    public long cnpjCliente = 0;
    public String nomeCliente = "";
    public String dataPagamento = "";
    public double valorPago = 0;
    public long idClientePreferencialLong = 0;

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public String getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(String dataemissao) {
        this.dataemissao = dataemissao;
    }
    
    public void setDataemissao(Date dataemissao) {
        this.dataemissao = Utils.formatDate(dataemissao);
    }

    public int getNumerocupom() {
        return numerocupom;
    }

    public void setNumerocupom(int numerocupom) {
        this.numerocupom = numerocupom;
    }

    public int getEcf() {
        return ecf;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean isLancamentomanual() {
        return lancamentomanual;
    }

    public void setLancamentomanual(boolean lancamentomanual) {
        this.lancamentomanual = lancamentomanual;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {        
        this.observacao = Utils.acertarTexto(observacao, 500, "IMPORTADO VR");
    }

    public int getId_situacaorecebercreditorotativo() {
        return id_situacaorecebercreditorotativo;
    }

    public void setId_situacaorecebercreditorotativo(int id_situacaorecebercreditorotativo) {
        this.id_situacaorecebercreditorotativo = id_situacaorecebercreditorotativo;
    }

    public int getId_clientepreferencial() {
        return id_clientepreferencial;
    }

    public void setId_clientepreferencial(int id_clientepreferencial) {
        this.id_clientepreferencial = id_clientepreferencial;
    }

    public String getDatavencimento() {
        return datavencimento;
    }

    public void setDatavencimento(String datavencimento) {
        this.datavencimento = datavencimento;
    }
    
    public void setDatavencimento(Date datavencimento) {
        this.datavencimento = Utils.formatDate(datavencimento);
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getParcela() {
        return parcela;
    }

    public void setParcela(int parcela) {
        this.parcela = parcela;
    }

    public double getValorjuros() {
        return valorjuros;
    }

    public void setValorjuros(double valorjuros) {
        this.valorjuros = valorjuros;
    }

    public int getId_boleto() {
        return id_boleto;
    }

    public void setId_boleto(int id_boleto) {
        this.id_boleto = id_boleto;
    }

    public int getId_tipolocalcobranca() {
        return id_tipolocalcobranca;
    }

    public void setId_tipolocalcobranca(int id_tipolocalcobranca) {
        this.id_tipolocalcobranca = id_tipolocalcobranca;
    }

    public double getValormulta() {
        return valormulta;
    }

    public void setValormulta(double valormulta) {
        this.valormulta = valormulta;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public boolean isExportado() {
        return exportado;
    }

    public void setExportado(boolean exportado) {
        this.exportado = exportado;
    }

    public Timestamp getDatahoraalteracao() {
        return datahoraalteracao;
    }

    public void setDatahoraalteracao(Timestamp datahoraalteracao) {
        this.datahoraalteracao = datahoraalteracao;
    }

    public long getCnpjCliente() {
        return cnpjCliente;
    }

    public void setCnpjCliente(long cnpjCliente) {
        this.cnpjCliente = cnpjCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = Utils.formatDate(dataPagamento);
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }
    
    
}
