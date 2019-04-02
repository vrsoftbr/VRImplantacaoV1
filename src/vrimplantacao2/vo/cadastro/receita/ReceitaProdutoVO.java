/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.receita;

/**
 *
 * @author lucasrafael
 */
public class ReceitaProdutoVO {

    private int id_receita;
    private int id_produto;
    private double rendimento;

    /**
     * @return the id_receita
     */
    public int getId_receita() {
        return id_receita;
    }

    /**
     * @param id_receita the id_receita to set
     */
    public void setId_receita(int id_receita) {
        this.id_receita = id_receita;
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
     * @return the rendimento
     */
    public double getRendimento() {
        return rendimento;
    }

    /**
     * @param rendimento the rendimento to set
     */
    public void setRendimento(double rendimento) {
        this.rendimento = rendimento;
    }
}
