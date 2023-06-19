/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.venda;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Michael
 */
public class PublicVendaValoresAgrupado {

    private int id_loja;//id_loja int4 NOT NULL,
    private int id_produto;//id_produto int4 NOT NULL,
    private Date data;//;data; date NOT NULL,
    private double mediaPreco;//precovenda numeric(11, 2) NOT NULL,
    private double somaQuantidade;//quantidade numeric(12, 3) NOT NULL,
    private int id_comprador;//id_comprador int4 NOT NULL,
    private double custocomimposto;//custocomimposto numeric(13, 4) NOT NULL,
    private double piscofins;//piscofins numeric(11, 2) NOT NULL,
    private double operacional;//operacional numeric(11, 2) NOT NULL,
    private double icmscredito;//icmscredito numeric(11, 2) NOT NULL,
    private double icmsdebito;//icmsdebito numeric(11, 2) NOT NULL,
    private double somaValoresTotal;//valortotal numeric(11, 2) NOT NULL,
    private double custosemimposto;//custosemimposto numeric(13, 4) NOT NULL,
    private boolean oferta;//oferta bool NOT NULL,
    private double perda;//perda numeric(14, 2) NOT NULL,
    private double customediosemimposto;//customediosemimposto numeric(13, 4) NOT NULL DEFAULT 0,
    private double customediocomimposto;//customediocomimposto numeric(13, 4) NOT NULL DEFAULT 0,
    private double piscofinscredito;//piscofinscredito numeric(11, 2) NOT NULL DEFAULT 0,
    private boolean cupomfiscal;//cupomfiscal bool NOT NULL DEFAULT true,    

    public PublicVendaValoresAgrupado(int id_loja, int id_produto, Date data, double mediaPreco, double somaQuantidade, int id_comprador, double custocomimposto, double piscofins, double operacional, double icmscredito, double icmsdebito, double somaValoresTotal, double custosemimposto, boolean oferta, double perda, double customediosemimposto, double customediocomimposto, double piscofinscredito, boolean cupomfiscal) {
        this.id_loja = id_loja;
        this.id_produto = id_produto;
        this.data = data;
        this.mediaPreco = mediaPreco;
        this.somaQuantidade = somaQuantidade;
        this.id_comprador = id_comprador;
        this.custocomimposto = custocomimposto;
        this.piscofins = piscofins;
        this.operacional = operacional;
        this.icmscredito = icmscredito;
        this.icmsdebito = icmsdebito;
        this.somaValoresTotal = somaValoresTotal;
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

    public double getMediaPreco() {
        return mediaPreco;
    }

    public double getSomaQuantidade() {
        return somaQuantidade;
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

    public double getSomaValoresTotal() {
        return somaValoresTotal;
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

    public void somarAtributos(PublicVendaValoresAgrupado outroProduto) {
        if (this.id_produto == outroProduto.id_produto && this.data.equals(outroProduto.data) && this.id_loja == outroProduto.id_loja) {
            this.somaQuantidade += outroProduto.somaQuantidade;
            this.somaValoresTotal += outroProduto.somaValoresTotal;
        }
    }
}
