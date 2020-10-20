/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro;

import java.util.Date;
/**
 *
 * @author Lucas
 */
public class LogCustoVO {

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
     * @return the custocomimposto
     */
    public double getCustocomimposto() {
        return custocomimposto;
    }

    /**
     * @param custocomimposto the custocomimposto to set
     */
    public void setCustocomimposto(double custocomimposto) {
        this.custocomimposto = custocomimposto;
    }

    /**
     * @return the custosemimposto
     */
    public double getCustosemimposto() {
        return custosemimposto;
    }

    /**
     * @param custosemimposto the custosemimposto to set
     */
    public void setCustosemimposto(double custosemimposto) {
        this.custosemimposto = custosemimposto;
    }

    /**
     * @return the datamovimento
     */
    public Date getDatamovimento() {
        return datamovimento;
    }

    /**
     * @param datamovimento the datamovimento to set
     */
    public void setDatamovimento(Date datamovimento) {
        this.datamovimento = datamovimento;
    }

    private int id_produto;
    private double custocomimposto;
    private double custosemimposto;
    private Date datamovimento;    
}
