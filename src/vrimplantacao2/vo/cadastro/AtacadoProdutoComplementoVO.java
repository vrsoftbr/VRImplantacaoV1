/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro;

/**
 *
 * @author Lucas
 */
public class AtacadoProdutoComplementoVO {

    private int idProduto;
    private int idLoja;
    private double precoVenda;
    
    /**
     * @return the idProduto
     */
    public int getIdProduto() {
        return idProduto;
    }

    /**
     * @param idProduto the idProduto to set
     */
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    /**
     * @return the idLoja
     */
    public int getIdLoja() {
        return idLoja;
    }

    /**
     * @param idLoja the idLoja to set
     */
    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    /**
     * @return the precoVenda
     */
    public double getPrecoVenda() {
        return precoVenda;
    }

    /**
     * @param precoVenda the precoVenda to set
     */
    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }    
}
