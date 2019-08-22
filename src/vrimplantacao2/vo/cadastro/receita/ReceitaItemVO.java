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
public class ReceitaItemVO {

    private int id_receita;
    private int id_produto;
    private int qtdembalagemreceita;
    private int qtdembalagemproduto;
    private boolean baixaestoque = true;
    private double fatorconversao = 1;
    private boolean embalagem = false;

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
     * @return the qtdembalagemreceita
     */
    public int getQtdembalagemreceita() {
        return qtdembalagemreceita;
    }

    /**
     * @param qtdembalagemreceita the qtdembalagemreceita to set
     */
    public void setQtdembalagemreceita(int qtdembalagemreceita) {
        this.qtdembalagemreceita = qtdembalagemreceita;
    }

    /**
     * @return the qtdembalagemproduto
     */
    public int getQtdembalagemproduto() {
        return qtdembalagemproduto;
    }

    /**
     * @param qtdembalagemproduto the qtdembalagemproduto to set
     */
    public void setQtdembalagemproduto(int qtdembalagemproduto) {
        this.qtdembalagemproduto = qtdembalagemproduto;
    }

    /**
     * @return the baixaestoque
     */
    public boolean isBaixaestoque() {
        return baixaestoque;
    }

    /**
     * @param baixaestoque the baixaestoque to set
     */
    public void setBaixaestoque(boolean baixaestoque) {
        this.baixaestoque = baixaestoque;
    }

    /**
     * @return the fatorconversao
     */
    public double getFatorconversao() {
        return fatorconversao;
    }

    /**
     * @param fatorconversao the fatorconversao to set
     */
    public void setFatorconversao(double fatorconversao) {
        this.fatorconversao = fatorconversao;
    }

    /**
     * @return the embalagem
     */
    public boolean isEmbalagem() {
        return embalagem;
    }

    /**
     * @param embalagem the embalagem to set
     */
    public void setEmbalagem(boolean embalagem) {
        this.embalagem = embalagem;
    }
}
