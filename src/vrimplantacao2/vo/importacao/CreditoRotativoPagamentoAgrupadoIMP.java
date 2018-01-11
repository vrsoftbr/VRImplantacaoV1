package vrimplantacao2.vo.importacao;

/**
 * Classe para importar os pagamentos do crédito rotativo agrupado.
 * @author Leandro
 */
public class CreditoRotativoPagamentoAgrupadoIMP {
    
    public String idCliente;
    public double valor;

    public CreditoRotativoPagamentoAgrupadoIMP() {
        this.valor = 0;
    }

    public CreditoRotativoPagamentoAgrupadoIMP(String idCliente, double valor) {
        this.idCliente = idCliente;
        this.valor = valor;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
    
}
