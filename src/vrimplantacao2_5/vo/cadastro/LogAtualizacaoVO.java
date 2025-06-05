/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.cadastro;

import java.util.Date;

/**
 *
 * @author Michael
 */
public class LogAtualizacaoVO {
    
    private String impSistema;
    private String impLoja;
    private String impId;
    private int lojaatual;
    private String descricao;
    private int coigoatual;
    private double preco;
    private Date dataAlteracao;
    private double estoque;
    private double custoComImposto;
    private double custoSemImposto;

    public String getImpSistema() {
        return impSistema;
    }

    public void setImpSistema(String impSistema) {
        this.impSistema = impSistema;
    }

    public String getImpLoja() {
        return impLoja;
    }

    public void setImpLoja(String impLoja) {
        this.impLoja = impLoja;
    }

    public String getImpId() {
        return impId;
    }

    public void setImpId(String impId) {
        this.impId = impId;
    }

    public int getLojaatual() {
        return lojaatual;
    }

    public void setLojaatual(int lojaatual) {
        this.lojaatual = lojaatual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCoigoatual() {
        return coigoatual;
    }

    public void setCoigoatual(int coigoatual) {
        this.coigoatual = coigoatual;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public double getEstoque() {
        return estoque;
    }

    public void setEstoque(double estoque) {
        this.estoque = estoque;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = custoComImposto;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = custoSemImposto;
    }
}
