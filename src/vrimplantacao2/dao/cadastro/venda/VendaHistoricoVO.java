package vrimplantacao2.dao.cadastro.venda;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 * Classe que representa do histórico da venda no VR.
 * @author Leandro
 */
public class VendaHistoricoVO {
    
    private int id = -1;
    private int id_loja = -1;
    private int id_produto = -1;
    private Date data;
    private double precoVenda = 0;
    private double quantidade = 1;
    private int id_comprador = 1;
    private double custoComImposto = 0;
    private double pisCofins = 0;
    private double operacional = 0;
    private double icmsCredito = 0;
    private double icmsDebito = 0;
    private double valorTotal = 0;
    private double custoSemImposto = 0;
    private boolean oferta = false;
    private double perda = 0;
    private double custoMedioSemImposto = 0;
    private double custoMedioComImposto = 0;
    private double pisCofinsCredito = 0;
    private boolean cupomfiscal = true;

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

    public int getId_produto() {
        return id_produto;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = MathUtils.round(precoVenda, 2, 99999999D);
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = MathUtils.round(quantidade, 3, 99999999D);
    }

    public int getId_comprador() {
        return id_comprador;
    }

    public void setId_comprador(int id_comprador) {
        this.id_comprador = id_comprador;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = MathUtils.round(custoComImposto, 4, 99999999D);
    }

    public double getPisCofins() {
        return pisCofins;
    }

    public void setPisCofins(double pisCofins) {
        this.pisCofins = MathUtils.round(pisCofins, 2, 99999999D);
    }

    public double getOperacional() {
        return operacional;
    }

    public void setOperacional(double operacional) {
        this.operacional = MathUtils.round(operacional, 2, 99999999D);
    }

    public double getIcmsCredito() {
        return icmsCredito;
    }

    public void setIcmsCredito(double icmsCredito) {
        this.icmsCredito = MathUtils.round(icmsCredito, 2, 99999999D);
    }

    public double getIcmsDebito() {
        return icmsDebito;
    }

    public void setIcmsDebito(double icmsDebito) {
        this.icmsDebito = MathUtils.round(icmsDebito, 2, 99999999D);
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = MathUtils.round(valorTotal, 2, 99999999D);
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = MathUtils.round(custoSemImposto, 4, 99999999D);
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
        this.perda = MathUtils.round(perda, 2, 99999999D);
    }

    public double getCustoMedioSemImposto() {
        return custoMedioSemImposto;
    }

    public void setCustoMedioSemImposto(double custoMedioSemImposto) {
        this.custoMedioSemImposto = MathUtils.round(custoMedioSemImposto, 4, 99999999D);
    }

    public double getCustoMedioComImposto() {
        return custoMedioComImposto;
    }

    public void setCustoMedioComImposto(double custoMedioComImposto) {
        this.custoMedioComImposto = MathUtils.round(custoMedioComImposto, 4, 99999999D);
    }

    public double getPisCofinsCredito() {
        return pisCofinsCredito;
    }

    public void setPisCofinsCredito(double pisCofinsCredito) {
        this.pisCofinsCredito = MathUtils.round(pisCofinsCredito, 2, 99999999D);
    }

    public boolean isCupomfiscal() {
        return cupomfiscal;
    }

    public void setCupomfiscal(boolean cupomfiscal) {
        this.cupomfiscal = cupomfiscal;
    }
    
    
}
