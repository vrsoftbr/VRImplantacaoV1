package vrimplantacao2.vo.cadastro;

import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class ProdutoAutomacaoDescontoVO {
    private int id;
    private long codigoBarras;
    private int id_loja;
    private double desconto;

    public int getId() {
        return id;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public int getId_loja() {
        return id_loja;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCodigoBarras(long codigoBarras) {
        if (codigoBarras <= 99999999999999L) {
            this.codigoBarras = codigoBarras;
        } else {
            this.codigoBarras = -2;
        }
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setDesconto(double desconto) {
        this.desconto = MathUtils.round(desconto, 2);
    }
    
    
}
