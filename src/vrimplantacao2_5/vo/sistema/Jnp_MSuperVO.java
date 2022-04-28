/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.sistema;

import java.util.Date;

/**
 *
 * @author Desenvolvimento
 */
public class Jnp_MSuperVO {
    
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public Date getDataInicioVenda() {
        return this.dataInicioVenda;
    }
    
    public Date getDataTerminoVenda() {
        return this.dataTerminoVenda;
    }
    
    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }
}
