package vrimplantacao2.vo.cadastro.fiscal.inventario;

/**
 *
 * @author Leandro
 */
public class ProdutoInventario {
    
    private int idProduto;
    private String descricao;
    private double precoVenda;
    private double custoComImposto;
    private double custoSemImposto;
    private double custoMedioComImposto;
    private double custoMedioSemImposto;
    private int idAliquotaCredito;
    private int idAliquotaDebito;

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
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

    public int getIdAliquotaCredito() {
        return idAliquotaCredito;
    }

    public void setIdAliquotaCredito(int idAliquotaCredito) {
        this.idAliquotaCredito = idAliquotaCredito;
    }

    public int getIdAliquotaDebito() {
        return idAliquotaDebito;
    }

    public void setIdAliquotaDebito(int idAliquotaDebito) {
        this.idAliquotaDebito = idAliquotaDebito;
    }
    
    
    
}
