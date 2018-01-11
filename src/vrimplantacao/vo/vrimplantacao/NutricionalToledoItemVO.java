/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

public class NutricionalToledoItemVO {
    private int id_nutricionaltoledo = 0;
    private int id_produto = 0;
    private String strID;
    private double id_produtoDouble = 0;

    /**
     * @return the id_nutricionaltoledo
     */
    public int getId_nutricionaltoledo() {
        return id_nutricionaltoledo;
    }

    /**
     * @param id_nutricionaltoledo the id_nutricionaltoledo to set
     */
    public void setId_nutricionaltoledo(int id_nutricionaltoledo) {
        this.id_nutricionaltoledo = id_nutricionaltoledo;
    }

    /**
     * @return the id_produto
     */
    public int getId_produto() {
        return id_produto;
    }

    /**
     * @param id_produto the id_produto to set
     */
    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    /**
     * @return the id_produtoDouble
     */
    public double getId_produtoDouble() {
        return id_produtoDouble;
    }

    /**
     * @param id_produtoDouble the id_produtoDouble to set
     */
    public void setId_produtoDouble(double id_produtoDouble) {
        this.id_produtoDouble = id_produtoDouble;
    }

    public String getStrID() {
        return strID;
    }

    public void setStrID(String strID) {
        this.strID = strID;
    }
    
    
    
}
