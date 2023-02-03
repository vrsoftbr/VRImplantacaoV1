/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.conversor.vo;

/**
 *
 * @author Desenvolvimento
 */
public class ControleDadosConvertidosVO {

    public String banco = "";
    public String nomeTabela = "";
    public String populada = "NÃO";

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNomeTabela() {
        return nomeTabela;
    }

    public void setNomeTabela(String nomeTabela) {
        this.nomeTabela = nomeTabela;
    }

    public String getPopulada() {
        return populada;
    }

    public void setPopulada(String populada) {
        this.populada = populada;
    }
}
