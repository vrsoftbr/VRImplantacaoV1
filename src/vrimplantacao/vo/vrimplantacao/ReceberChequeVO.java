/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author lucasrafael
 */
public class ReceberChequeVO {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    public int id_loja = 1;
    public int id = 0;
    public Long cpf;
    public int numerocheque;
    public int id_banco;
    public String agencia = "";
    public String conta = "";
    public String data = "";    
    public int id_plano = 1;
    public int numerocupom = 0;
    public int ecf = 0;
    public double valor = 0;
    public String datadeposito = "";
    public boolean lancamentomanual = true;
    public String rg = "";
    public String telefone = "";
    public String nome = "";
    public String observacao;
    public int id_situacaorecebercheque = 0;
    public int id_tipolocalcobranca = 0;
    public String cmc7 = "";
    public String datadevolucao = "";
    public int id_tipoalinea = 0;
    public int id_tipoinscricao = 1;
    public Date dataenviocobranca = null;
    public double valorpagarfornecedor = 0;
    public int id_boleto = 0;
    public String operadorclientebloqueado = "";
    public String operadorexcedelimite = "";
    public String operadorproblemacheque = "";
    public String operadorchequebloqueado = "";
    public double valorjuros = 0;
    public int id_tipovistaprazo = 0;
    public String justificativa = "";
    public double valoracrescimo = 0;
    public double valorinicial = 0;
    public int idCliente = 0;
    private List<ReceberChequeItemVO> vBaixa = new ArrayList<>();
    private List<ReceberChequeHistoricoVO> vHistorico = new ArrayList<>();

    public List<ReceberChequeHistoricoVO> getvHistorico() {
        return vHistorico;
    }

    public void setvHistorico(List<ReceberChequeHistoricoVO> vHistorico) {
        this.vHistorico = vHistorico;
    }

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

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        if (cpf > 99999999999999L) {
            cpf = 0L;
        }
        this.cpf = cpf;
    }

    public int getNumerocheque() {
        return numerocheque;
    }

    public void setNumerocheque(int numerocheque) {
        this.numerocheque = numerocheque;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
    public void setData(Date data) {
        this.data = DATE_FORMAT.format(data);
    }

    public int getId_plano() {
        return id_plano;
    }

    public void setId_plano(int id_plano) {
        this.id_plano = id_plano;
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
        this.valor = MathUtils.round(valor, 2);
        if (this.getValorinicial() == 0) {
            this.setValorinicial(this.valor);
        } 
    }

    public String getDatadeposito() {
        return datadeposito;
    }

    public void setDatadeposito(String datadeposito) {
        this.datadeposito = datadeposito;
    }
    
    public void setDatadeposito(Date datadeposito) {
        this.datadeposito = DATE_FORMAT.format(datadeposito);
    }

    public boolean isLancamentomanual() {
        return lancamentomanual;
    }

    public void setLancamentomanual(boolean lancamentomanual) {
        this.lancamentomanual = lancamentomanual;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = Utils.formataNumero(rg, 20);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.acertarTexto(telefone, 14, "(00)0000-0000");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 40, "SEM NOME");
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getId_situacaorecebercheque() {
        return id_situacaorecebercheque;
    }

    public void setId_situacaorecebercheque(int id_situacaorecebercheque) {
        this.id_situacaorecebercheque = id_situacaorecebercheque;
    }

    public int getId_tipolocalcobranca() {
        return id_tipolocalcobranca;
    }

    public void setId_tipolocalcobranca(int id_tipolocalcobranca) {
        this.id_tipolocalcobranca = id_tipolocalcobranca;
    }

    public String getCmc7() {
        return cmc7;
    }

    public void setCmc7(String cmc7) {
        this.cmc7 = Utils.formataNumero(cmc7, 34);
    }

    public String getDatadevolucao() {
        return datadevolucao;
    }

    public void setDatadevolucao(String datadevolucao) {
        this.datadevolucao = datadevolucao;
    }
    
    public void setDatadevolucao(Date datadevolucao) {
        this.datadevolucao = DATE_FORMAT.format(datadevolucao);
    }

    public int getId_tipoalinea() {
        return id_tipoalinea;
    }

    public void setId_tipoalinea(int id_tipoalinea) {
        this.id_tipoalinea = id_tipoalinea;
    }

    public int getId_tipoinscricao() {
        return id_tipoinscricao;
    }

    public void setId_tipoinscricao(int id_tipoinscricao) {
        this.id_tipoinscricao = id_tipoinscricao;
    }

    public Date getDataenviocobranca() {
        return dataenviocobranca;
    }

    public void setDataenviocobranca(Date dataenviocobranca) {
        this.dataenviocobranca = dataenviocobranca;
    }

    public double getValorpagarfornecedor() {
        return valorpagarfornecedor;
    }

    public void setValorpagarfornecedor(double valorpagarfornecedor) {
        this.valorpagarfornecedor = MathUtils.round(valorpagarfornecedor, 2);
    }

    public int getId_boleto() {
        return id_boleto;
    }

    public void setId_boleto(int id_boleto) {
        this.id_boleto = id_boleto;
    }

    public String getOperadorclientebloqueado() {
        return operadorclientebloqueado;
    }

    public void setOperadorclientebloqueado(String operadorclientebloqueado) {
        this.operadorclientebloqueado = Utils.acertarTexto(operadorclientebloqueado, 40);
    }

    public String getOperadorexcedelimite() {
        return operadorexcedelimite;
    }

    public void setOperadorexcedelimite(String operadorexcedelimite) {
        this.operadorexcedelimite = Utils.acertarTexto(operadorexcedelimite, 40);
    }

    public String getOperadorproblemacheque() {
        return operadorproblemacheque;
    }

    public void setOperadorproblemacheque(String operadorproblemacheque) {
        this.operadorproblemacheque = Utils.acertarTexto(operadorproblemacheque, 40);
    }

    public String getOperadorchequebloqueado() {
        return operadorchequebloqueado;
    }

    public void setOperadorchequebloqueado(String operadorchequebloqueado) {
        this.operadorchequebloqueado = Utils.acertarTexto(operadorchequebloqueado, 40);
    }

    public double getValorjuros() {
        return valorjuros;
    }

    public void setValorjuros(double valorjuros) {
        this.valorjuros = valorjuros;
    }

    public int getId_tipovistaprazo() {
        return id_tipovistaprazo;
    }

    public void setId_tipovistaprazo(int id_tipovistaprazo) {
        this.id_tipovistaprazo = id_tipovistaprazo;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = Utils.acertarTexto(justificativa, 50);
    }

    public double getValoracrescimo() {
        return valoracrescimo;
    }

    public void setValoracrescimo(double valoracrescimo) {
        this.valoracrescimo =  MathUtils.round(valoracrescimo, 2);
    }

    public double getValorinicial() {
        return valorinicial;
    }

    public void setValorinicial(double valorinicial) {
        this.valorinicial =  MathUtils.round(valorinicial, 2);
    }

    public List<ReceberChequeItemVO> getvBaixa() {
        return vBaixa;
    }

    public void setvBaixa(List<ReceberChequeItemVO> vBaixa) {
        this.vBaixa = vBaixa;
    }    
    
    //Assistentes da integração
    public String impSistemaId;
    public String impLojaId;
    public String impId;
    
    public String getChave() {
        return impSistemaId + "-" + impLojaId + "-" + impId;
    }
    
}
