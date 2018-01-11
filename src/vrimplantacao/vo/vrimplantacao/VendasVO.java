/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author lucasrafael
 */
public class VendasVO {
    public int id_loja = 1;
    public double id_produto = 0;
    public String data = "";
    public double precovenda = 0;
    public double quantidade = 1;
    public int id_comprador = 1;
    public double custocomimposto = 0;
    public double piscofins = 0;
    public double operacional = 0;
    public double icmscredito = 0;
    public double icmsdebito = 0;
    public double valortotal = 0;
    public double custosemimposto = 0;
    public boolean oferta = false;
    public double perda = 0;
    public double customediosemimposto = 0;
    public double customediocomimposto = 0;
    public double piscofinscredito = 0;
    public boolean cupomfiscal = false;

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public double getId_produto() {
        return id_produto;
    }

    public void setId_produto(double id_produto) {
        this.id_produto = id_produto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getPrecovenda() {
        return precovenda;
    }

    public void setPrecovenda(double precovenda) {
        this.precovenda = precovenda;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public int getId_comprador() {
        return id_comprador;
    }

    public void setId_comprador(int id_comprador) {
        this.id_comprador = id_comprador;
    }

    public double getCustocomimposto() {
        return custocomimposto;
    }

    public void setCustocomimposto(double custocomimposto) {
        this.custocomimposto = custocomimposto;
    }

    public double getPiscofins() {
        return piscofins;
    }

    public void setPiscofins(double piscofins) {
        this.piscofins = piscofins;
    }

    public double getOperacional() {
        return operacional;
    }

    public void setOperacional(double operacional) {
        this.operacional = operacional;
    }

    public double getIcmscredito() {
        return icmscredito;
    }

    public void setIcmscredito(double icmscredito) {
        this.icmscredito = icmscredito;
    }

    public double getIcmsdebito() {
        return icmsdebito;
    }

    public void setIcmsdebito(double icmsdebito) {
        this.icmsdebito = icmsdebito;
    }

    public double getValortotal() {
        return valortotal;
    }

    public void setValortotal(double valortotal) {
        this.valortotal = valortotal;
    }

    public double getCustosemimposto() {
        return custosemimposto;
    }

    public void setCustosemimposto(double custosemimposto) {
        this.custosemimposto = custosemimposto;
    }

    public boolean isOferta() {
        return oferta;
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    public double getPerda() {
        return perda;
    }

    public void setPerda(double perda) {
        this.perda = perda;
    }

    public double getCustomediosemimposto() {
        return customediosemimposto;
    }

    public void setCustomediosemimposto(double customediosemimposto) {
        this.customediosemimposto = customediosemimposto;
    }

    public double getCustomediocomimposto() {
        return customediocomimposto;
    }

    public void setCustomediocomimposto(double customediocomimposto) {
        this.customediocomimposto = customediocomimposto;
    }

    public double getPiscofinscredito() {
        return piscofinscredito;
    }

    public void setPiscofinscredito(double piscofinscredito) {
        this.piscofinscredito = piscofinscredito;
    }

    public boolean isCupomfiscal() {
        return cupomfiscal;
    }

    public void setCupomfiscal(boolean cupomfiscal) {
        this.cupomfiscal = cupomfiscal;
    }
    
    
}
