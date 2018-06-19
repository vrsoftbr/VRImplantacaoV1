/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.pdv.acumulador;

/**
 *
 * @author lucasrafael
 */
public class AcumuladorLayoutRetornoVO {

    private int idAcumuladorLayout;
    private int idAcumulador;
    private int retorno;
    private int titulo;

    public int getIdAcumuladorLayout() {
        return idAcumuladorLayout;
    }

    public void setIdAcumuladorLayout(int idAcumuladorLayout) {
        this.idAcumuladorLayout = idAcumuladorLayout;
    }

    public int getIdAcumulador() {
        return idAcumulador;
    }

    public void setIdAcumulador(int idAcumulador) {
        this.idAcumulador = idAcumulador;
    }

    public int getRetorno() {
        return retorno;
    }

    public void setRetorno(int retorno) {
        this.retorno = retorno;
    }

    public int getTitulo() {
        return titulo;
    }

    public void setTitulo(int titulo) {
        this.titulo = titulo;
    }
}
