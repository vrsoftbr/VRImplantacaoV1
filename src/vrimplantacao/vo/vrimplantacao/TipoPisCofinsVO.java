/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author handerson
 */
public class TipoPisCofinsVO {

    public int id = 0;
    public String descricao = "";
    public int cst = 0;
    public double valorPis = 0;
    public double valorCofins = 0;
    public boolean substituicao = false;
    public double reduzidoCredito = 0;
    public int csa = 0;
    public int tipoCredito = 0;
    public int idTipoEntradaSaida = 0;
    public String tipoEntradaSaida = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCst() {
        return cst;
    }

    public void setCst(int cst) {
        this.cst = cst;
    }

    public double getValorPis() {
        return valorPis;
    }

    public void setValorPis(double valorPis) {
        this.valorPis = valorPis;
    }

    public double getValorCofins() {
        return valorCofins;
    }

    public void setValorCofins(double valorCofins) {
        this.valorCofins = valorCofins;
    }

    public boolean isSubstituicao() {
        return substituicao;
    }

    public void setSubstituicao(boolean substituicao) {
        this.substituicao = substituicao;
    }

    public double getReduzidoCredito() {
        return reduzidoCredito;
    }

    public void setReduzidoCredito(double reduzidoCredito) {
        this.reduzidoCredito = reduzidoCredito;
    }

    public int getCsa() {
        return csa;
    }

    public void setCsa(int csa) {
        this.csa = csa;
    }

    public int getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(int tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public int getIdTipoEntradaSaida() {
        return idTipoEntradaSaida;
    }

    public void setIdTipoEntradaSaida(int idTipoEntradaSaida) {
        this.idTipoEntradaSaida = idTipoEntradaSaida;
    }

    public String getTipoEntradaSaida() {
        return tipoEntradaSaida;
    }

    public void setTipoEntradaSaida(String tipoEntradaSaida) {
        this.tipoEntradaSaida = tipoEntradaSaida;
    }
    
    
}
