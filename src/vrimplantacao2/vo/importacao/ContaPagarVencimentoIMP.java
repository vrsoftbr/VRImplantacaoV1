package vrimplantacao2.vo.importacao;

import java.util.Date;

/**
 *
 * @author Leandro
 */
public class ContaPagarVencimentoIMP {
    private String id;
    private ContaPagarIMP contaPagar;
    private Date vencimento;
    private double valor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContaPagarIMP getContaPagar() {
        return contaPagar;
    }

    public void setContaPagar(ContaPagarIMP contaPagar) {
        this.contaPagar = contaPagar;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
    
}
