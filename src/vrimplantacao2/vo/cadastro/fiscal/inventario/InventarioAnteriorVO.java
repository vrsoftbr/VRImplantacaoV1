/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.fiscal.inventario;

import java.util.Date;

/**
 *
 * @author lucasrafael
 */
public class InventarioAnteriorVO {

    private String sistema;
    private String idLoja;
    private String id;
    private String codigoAnteior;
    private String codigoAtual;
    private Date data;
    private Date datageracao;
    private String descricao;
    private double precoVenda;
    private double quantidade;
    private double custoComImposto;
    private double custoSemImposto;
    private String idAliquotaCredito;
    private String idAliquotadebito;
    private double pis;
    private double cofins;
    private double custoMedioComImposto;
    private double custoMedioSemImposto;
    private InventarioVO idAtual;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }

    public String getCodigoAnteior() {
        return codigoAnteior;
    }
    
    public void setCodigoAnteior(String codigoAnteior) {
        this.codigoAnteior = codigoAnteior;
    }

    public String getCodigoAtual() {
        return codigoAtual;
    }
    
    public void setCodigoAtual(String codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getDatageracao() {
        return datageracao;
    }

    public void setDatageracao(Date datageracao) {
        this.datageracao = datageracao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
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

    public String getIdAliquotaCredito() {
        return idAliquotaCredito;
    }

    public void setIdAliquotaCredito(String idAliquotaCredito) {
        this.idAliquotaCredito = idAliquotaCredito;
    }

    public String getIdAliquotadebito() {
        return idAliquotadebito;
    }

    public void setIdAliquotadebito(String idAliquotadebito) {
        this.idAliquotadebito = idAliquotadebito;
    }

    public double getPis() {
        return pis;
    }

    public void setPis(double pis) {
        this.pis = pis;
    }

    public double getCofins() {
        return cofins;
    }

    public void setCofins(double cofins) {
        this.cofins = cofins;
    }

    public double getCustoMedioComImposto() {
        return custoMedioComImposto;
    }

    public void setCustoMedioComImposto(double custoMedioComImposto) {
        this.custoMedioComImposto = custoMedioComImposto;
    }

    public double getCustoMedioSemImposto() {
        return custoMedioSemImposto;
    }

    public void setCustoMedioSemImposto(double custoMedioSemImposto) {
        this.custoMedioSemImposto = custoMedioSemImposto;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public InventarioVO getIdAtual() {
        return idAtual;
    }

    public void setIdAtual(InventarioVO idAtual) {
        this.idAtual = idAtual;
    }
}
