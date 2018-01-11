/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author lucasrafael
 */
public class PagarOutrasDespesasVencimentoVO {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    public int id_pagaroutrasdespesas = 0;
    public String datavencimento = "";
    public Date dataVencimento = null;
    public double valor = 0;    

    public int getId_pagaroutrasdespesas() {
        return id_pagaroutrasdespesas;
    }

    public void setId_pagaroutrasdespesas(int id_pagaroutrasdespesas) {
        this.id_pagaroutrasdespesas = id_pagaroutrasdespesas;
    }

    public String getDatavencimento() {
        return datavencimento;
    }

    public void setDatavencimento(String datavencimento) {
        this.datavencimento = datavencimento;
    }
    
    public void setDatavencimento(Date datavencimento) {
        this.datavencimento = DATE_FORMAT.format(datavencimento);
        this.dataVencimento = datavencimento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
    
    
}
