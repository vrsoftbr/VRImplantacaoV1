/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;
import vrimplantacao.utils.Utils;

/**
 *
 * @author Lucas
 */
public class ReceberVendaPrazoVO {

    private int id;
    private int id_loja;
    private int id_clienteeventual;
    private Date dataemissao;
    private Date datavencimento;
    private double valor;
    private double valorliquido;
    private String observacao;
    private int id_situacaorecebervendaprazo;
    private int id_tipolocalcobranca;
    private int numeronota;
    private double impostorenda = 0;
    private double pis = 0;
    private double cofins = 0;
    private double csll = 0;
    private double id_tiposaida = 0;
    private boolean lancamentomanual = false;
    private double valorjuros = 0;
    private String justificativa = "";
    private int numeroparcela = 1;
    private boolean exportado = false;
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the id_loja
     */
    public int getId_loja() {
        return id_loja;
    }

    /**
     * @param id_loja the id_loja to set
     */
    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    /**
     * @return the id_clienteeventual
     */
    public int getId_clienteeventual() {
        return id_clienteeventual;
    }

    /**
     * @param id_clienteeventual the id_clienteeventual to set
     */
    public void setId_clienteeventual(int id_clienteeventual) {
        this.id_clienteeventual = id_clienteeventual;
    }

    /**
     * @return the dataemissao
     */
    public Date getDataemissao() {
        return dataemissao;
    }

    /**
     * @param dataemissao the dataemissao to set
     */
    public void setDataemissao(Date dataemissao) {
        this.dataemissao = dataemissao;
    }

    /**
     * @return the datavencimento
     */
    public Date getDatavencimento() {
        return datavencimento;
    }

    /**
     * @param datavencimento the datavencimento to set
     */
    public void setDatavencimento(Date datavencimento) {
        this.datavencimento = datavencimento;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the valorliquido
     */
    public double getValorliquido() {
        return valorliquido;
    }

    /**
     * @param valorliquido the valorliquido to set
     */
    public void setValorliquido(double valorliquido) {
        this.valorliquido = valorliquido;
    }

    /**
     * @return the observacao
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * @param observacao the observacao to set
     */
    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao);
    }

    /**
     * @return the id_situacaorecebervendaprazo
     */
    public int getId_situacaorecebervendaprazo() {
        return id_situacaorecebervendaprazo;
    }

    /**
     * @param id_situacaorecebervendaprazo the id_situacaorecebervendaprazo to set
     */
    public void setId_situacaorecebervendaprazo(int id_situacaorecebervendaprazo) {
        this.id_situacaorecebervendaprazo = id_situacaorecebervendaprazo;
    }

    /**
     * @return the id_tipolocalcobranca
     */
    public int getId_tipolocalcobranca() {
        return id_tipolocalcobranca;
    }

    /**
     * @param id_tipolocalcobranca the id_tipolocalcobranca to set
     */
    public void setId_tipolocalcobranca(int id_tipolocalcobranca) {
        this.id_tipolocalcobranca = id_tipolocalcobranca;
    }

    /**
     * @return the numeronota
     */
    public int getNumeronota() {
        return numeronota;
    }

    /**
     * @param numeronota the numeronota to set
     */
    public void setNumeronota(int numeronota) {
        this.numeronota = numeronota;
    }

    /**
     * @return the impostorenda
     */
    public double getImpostorenda() {
        return impostorenda;
    }

    /**
     * @param impostorenda the impostorenda to set
     */
    public void setImpostorenda(double impostorenda) {
        this.impostorenda = impostorenda;
    }

    /**
     * @return the pis
     */
    public double getPis() {
        return pis;
    }

    /**
     * @param pis the pis to set
     */
    public void setPis(double pis) {
        this.pis = pis;
    }

    /**
     * @return the cofins
     */
    public double getCofins() {
        return cofins;
    }

    /**
     * @param cofins the cofins to set
     */
    public void setCofins(double cofins) {
        this.cofins = cofins;
    }

    /**
     * @return the csll
     */
    public double getCsll() {
        return csll;
    }

    /**
     * @param csll the csll to set
     */
    public void setCsll(double csll) {
        this.csll = csll;
    }

    /**
     * @return the id_tiposaida
     */
    public double getId_tiposaida() {
        return id_tiposaida;
    }

    /**
     * @param id_tiposaida the id_tiposaida to set
     */
    public void setId_tiposaida(double id_tiposaida) {
        this.id_tiposaida = id_tiposaida;
    }

    /**
     * @return the lancamentomanual
     */
    public boolean isLancamentomanual() {
        return lancamentomanual;
    }

    /**
     * @param lancamentomanual the lancamentomanual to set
     */
    public void setLancamentomanual(boolean lancamentomanual) {
        this.lancamentomanual = lancamentomanual;
    }

    /**
     * @return the valorjuros
     */
    public double getValorjuros() {
        return valorjuros;
    }

    /**
     * @param valorjuros the valorjuros to set
     */
    public void setValorjuros(double valorjuros) {
        this.valorjuros = valorjuros;
    }

    /**
     * @return the justificativa
     */
    public String getJustificativa() {
        return justificativa;
    }

    /**
     * @param justificativa the justificativa to set
     */
    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    /**
     * @return the numeroparcela
     */
    public int getNumeroparcela() {
        return numeroparcela;
    }

    /**
     * @param numeroparcela the numeroparcela to set
     */
    public void setNumeroparcela(int numeroparcela) {
        this.numeroparcela = numeroparcela;
    }

    /**
     * @return the exportado
     */
    public boolean isExportado() {
        return exportado;
    }

    /**
     * @param exportado the exportado to set
     */
    public void setExportado(boolean exportado) {
        this.exportado = exportado;
    }
}
