package vrimplantacao2.dao.cadastro.venda;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class VendaHistoricoIMP {
    
    private String idProduto;
    private Date data;
    private double precoVenda = 0;
    private double quantidade = 0;
    private double custoComImposto = 0;
    private double custoSemImposto = 0;
    private double pisCofinsDebito = 0;
    private double pisCofinsCredito = 0;
    private double operacional = 0;
    private double icmsCredito = 0;
    private double icmsDebito = 0;
    private double valorTotal = 0;
    private boolean oferta = false;
    private boolean cupomFiscal = false;

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
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
        this.precoVenda = MathUtils.round(precoVenda, 2);
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
        this.custoComImposto = MathUtils.round(custoComImposto, 4);
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = MathUtils.round(custoSemImposto, 4);
    }

    public double getPisCofinsDebito() {
        return pisCofinsDebito;
    }

    public void setPisCofinsDebito(double pisCofinsDebito) {
        this.pisCofinsDebito = pisCofinsDebito;
    }

    public double getPisCofinsCredito() {
        return pisCofinsCredito;
    }

    public void setPisCofinsCredito(double pisCofinsCredito) {
        this.pisCofinsCredito = pisCofinsCredito;
    }

    public double getOperacional() {
        return operacional;
    }

    public void setOperacional(double operacional) {
        this.operacional = operacional;
    }

    public double getIcmsCredito() {
        return icmsCredito;
    }

    public void setIcmsCredito(double icmsCredito) {
        this.icmsCredito = icmsCredito;
    }

    public double getIcmsDebito() {
        return icmsDebito;
    }

    public void setIcmsDebito(double icmsDebito) {
        this.icmsDebito = icmsDebito;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public boolean isOferta() {
        return oferta;
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    public boolean isCupomFiscal() {
        return cupomFiscal;
    }

    public void setCupomFiscal(boolean cupomFiscal) {
        this.cupomFiscal = cupomFiscal;
    }
    
    
}
