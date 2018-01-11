package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class PagarOutrasDespesasVencimentoVO {
    private int id;
    private PagarOutrasDespesasVO pagarOutrasDespesas;
    private Date dataVencimento = new Date();
    private double valor = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PagarOutrasDespesasVO getPagarOutrasDespesas() {
        return pagarOutrasDespesas;
    }

    public void setPagarOutrasDespesas(PagarOutrasDespesasVO pagarOutrasDespesas) {
        this.pagarOutrasDespesas = pagarOutrasDespesas;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        if (dataVencimento != null) {
            this.dataVencimento = dataVencimento;
        } else {
            if (pagarOutrasDespesas != null) {
                this.dataVencimento = this.pagarOutrasDespesas.getDataEmissao();
            } else {
                this.dataVencimento = new Date();
            } 
        }
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 2, 999999999D);
    }
    
    
}
