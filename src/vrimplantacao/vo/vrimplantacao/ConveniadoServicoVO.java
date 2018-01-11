/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author lucasrafael
 */
public class ConveniadoServicoVO {
    public int id_conveniado = 0;
    public int id_tiposervicoconvenio = 1;
    public double valor = 0;
    public double valordesconto = 0;

    public int getId_conveniado() {
        return id_conveniado;
    }

    public void setId_conveniado(int id_conveniado) {
        this.id_conveniado = id_conveniado;
    }

    public int getId_tiposervicoconvenio() {
        return id_tiposervicoconvenio;
    }

    public void setId_tiposervicoconvenio(int id_tiposervicoconvenio) {
        this.id_tiposervicoconvenio = id_tiposervicoconvenio;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValordesconto() {
        return valordesconto;
    }

    public void setValordesconto(double valordesconto) {
        this.valordesconto = valordesconto;
    }
}
