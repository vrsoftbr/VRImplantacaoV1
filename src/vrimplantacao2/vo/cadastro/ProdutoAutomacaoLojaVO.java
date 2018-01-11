package vrimplantacao2.vo.cadastro;

import vrimplantacao2.utils.MathUtils;

/**
 * 
 * @author Leandro
 */
public class ProdutoAutomacaoLojaVO {
    
    private int id;
    private long codigoBarras;
    private double precoVenda;
    private int id_loja;

    public int getId() {
        return id;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public int getId_loja() {
        return id_loja;
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

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = MathUtils.round(precoVenda, 2);
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }
    
    
    
}
