package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class PagarOutrasDespesasVencimentoVO {
    private int id;
    private int idPagarOutrasDespesas;
    private Date dataVencimento = new Date();
    private double valor = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPagarOutrasDespesas() {
        return idPagarOutrasDespesas;
    }

    public void setIdPagarOutrasDespesas(int idPagarOutrasDespesas) {
        this.idPagarOutrasDespesas = idPagarOutrasDespesas;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        if (dataVencimento != null) {
            this.dataVencimento = dataVencimento;
        }
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 2, 999999999D);
    }
    
    
}
