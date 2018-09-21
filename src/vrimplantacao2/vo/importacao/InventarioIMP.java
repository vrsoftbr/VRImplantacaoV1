package vrimplantacao2.vo.importacao;

import java.util.Date;

/**
 *
 * @author lucasrafael
 */
public class InventarioIMP {

    private String id;
    private String idProduto;
    private Date data = new Date();
    private String descricao;
    private double precoVenda;
    private double quantidade;
    private double custoComImposto;
    private double custoSemImposto;
    private String idAliquotaCredito;
    private String idAliquotaDebito;
    
    private String cstCredito;
    private double aliquotaCredito;
    private double reduzidoCredito;
    private String cstDebito;
    private double aliquotaDebito;
    private double reduzidoDebito;
    
    private double pis;
    private double cofins;
    private double custoMedioComImposto;
    private double custoMedioSemImposto;

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

    public String getIdAliquotaDebito() {
        return idAliquotaDebito;
    }

    public void setIdAliquotaDebito(String idAliquotaDebito) {
        this.idAliquotaDebito = idAliquotaDebito;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCstCredito() {
        return cstCredito;
    }

    public void setCstCredito(String cstCredito) {
        this.cstCredito = cstCredito;
    }

    public double getAliquotaCredito() {
        return aliquotaCredito;
    }

    public void setAliquotaCredito(double aliquotaCredito) {
        this.aliquotaCredito = aliquotaCredito;
    }

    public double getReduzidoCredito() {
        return reduzidoCredito;
    }

    public void setReduzidoCredito(double reduzidoCredito) {
        this.reduzidoCredito = reduzidoCredito;
    }

    public String getCstDebito() {
        return cstDebito;
    }

    public void setCstDebito(String cstDebito) {
        this.cstDebito = cstDebito;
    }

    public double getAliquotaDebito() {
        return aliquotaDebito;
    }

    public void setAliquotaDebito(double aliquotaDebito) {
        this.aliquotaDebito = aliquotaDebito;
    }

    public double getReduzidoDebito() {
        return reduzidoDebito;
    }

    public void setReduzidoDebito(double reduzidoDebito) {
        this.reduzidoDebito = reduzidoDebito;
    }
    
}