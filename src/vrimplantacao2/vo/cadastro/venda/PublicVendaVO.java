/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.venda;

import java.util.Date;

/**
 *
 * @author Desenvolvimento
 */
public class PublicVendaVO {

    private int id_loja;//id_loja int4 NOT NULL,
    private int id_produto;//id_produto int4 NOT NULL,
    private Date data;//;data; date NOT NULL,
    private double precovenda;//precovenda numeric(11, 2) NOT NULL,
    private double quantidade;//quantidade numeric(12, 3) NOT NULL,
    private int id_comprador;//id_comprador int4 NOT NULL,
    private double custocomimposto;//custocomimposto numeric(13, 4) NOT NULL,
    private double piscofins;//piscofins numeric(11, 2) NOT NULL,
    private double operacional;//operacional numeric(11, 2) NOT NULL,
    private double icmscredito;//icmscredito numeric(11, 2) NOT NULL,
    private double icmsdebito;//icmsdebito numeric(11, 2) NOT NULL,
    private double valortotal;//valortotal numeric(11, 2) NOT NULL,
    private double custosemimposto;//custosemimposto numeric(13, 4) NOT NULL,
    private boolean oferta;//oferta bool NOT NULL,
    private double perda;//perda numeric(14, 2) NOT NULL,
    private double customediosemimposto;//customediosemimposto numeric(13, 4) NOT NULL DEFAULT 0,
    private double customediocomimposto;//customediocomimposto numeric(13, 4) NOT NULL DEFAULT 0,
    private double piscofinscredito;//piscofinscredito numeric(11, 2) NOT NULL DEFAULT 0,
    private boolean cupomfiscal;//cupomfiscal bool NOT NULL DEFAULT true,     

    public PublicVendaVO(int id_loja, int id_produto, Date data, double precovenda, 
            double quantidade, int id_comprador, double custocomimposto, double piscofins, 
            double operacional, double icmscredito, double icmsdebito, double valortotal, 
            double custosemimposto, boolean oferta, double perda, double customediosemimposto, 
            double customediocomimposto, double piscofinscredito, boolean cupomfiscal) {
        this.id_loja = id_loja;
        this.id_produto = id_produto;
        this.data = data;
        this.precovenda = precovenda;
        this.quantidade = quantidade;
        this.id_comprador = id_comprador;
        this.custocomimposto = custocomimposto;
        this.piscofins = piscofins;
        this.operacional = operacional;
        this.icmscredito = icmscredito;
        this.icmsdebito = icmsdebito;
        this.valortotal = valortotal;
        this.custosemimposto = custosemimposto;
        this.oferta = oferta;
        this.perda = perda;
        this.customediosemimposto = customediosemimposto;
        this.customediocomimposto = customediocomimposto;
        this.piscofinscredito = piscofinscredito;
        this.cupomfiscal = cupomfiscal;
    }

    public int getId_loja() {
        return id_loja;
    }

    public int getId_produto() {
        return id_produto;
    }

    public Date getData() {
        return data;
    }

    public double getPrecovenda() {
        return precovenda;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public int getId_comprador() {
        return id_comprador;
    }

    public double getCustocomimposto() {
        return custocomimposto;
    }

    public double getPiscofins() {
        return piscofins;
    }

    public double getOperacional() {
        return operacional;
    }

    public double getIcmscredito() {
        return icmscredito;
    }

    public double getIcmsdebito() {
        return icmsdebito;
    }

    public double getValortotal() {
        return valortotal;
    }

    public double getCustosemimposto() {
        return custosemimposto;
    }

    public boolean isOferta() {
        return oferta;
    }

    public double getPerda() {
        return perda;
    }

    public double getCustomediosemimposto() {
        return customediosemimposto;
    }

    public double getCustomediocomimposto() {
        return customediocomimposto;
    }

    public double getPiscofinscredito() {
        return piscofinscredito;
    }

    public boolean isCupomfiscal() {
        return cupomfiscal;
    }    

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setPrecovenda(double precovenda) {
        this.precovenda = precovenda;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setId_comprador(int id_comprador) {
        this.id_comprador = id_comprador;
    }

    public void setCustocomimposto(double custocomimposto) {
        this.custocomimposto = custocomimposto;
    }

    public void setPiscofins(double piscofins) {
        this.piscofins = piscofins;
    }

    public void setOperacional(double operacional) {
        this.operacional = operacional;
    }

    public void setIcmscredito(double icmscredito) {
        this.icmscredito = icmscredito;
    }

    public void setIcmsdebito(double icmsdebito) {
        this.icmsdebito = icmsdebito;
    }

    public void setValortotal(double valortotal) {
        this.valortotal = valortotal;
    }

    public void setCustosemimposto(double custosemimposto) {
        this.custosemimposto = custosemimposto;
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    public void setPerda(double perda) {
        this.perda = perda;
    }

    public void setCustomediosemimposto(double customediosemimposto) {
        this.customediosemimposto = customediosemimposto;
    }

    public void setCustomediocomimposto(double customediocomimposto) {
        this.customediocomimposto = customediocomimposto;
    }

    public void setPiscofinscredito(double piscofinscredito) {
        this.piscofinscredito = piscofinscredito;
    }

    public void setCupomfiscal(boolean cupomfiscal) {
        this.cupomfiscal = cupomfiscal;
    }
    
}
