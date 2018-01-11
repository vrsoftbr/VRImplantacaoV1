/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao.utils.Utils;

/**
 *
 * @author handerson
 */
public class MercadologicoVO {

    public long id = 0;
    public String descricao = "SEM DESCRICAO";
    public int mercadologico1 = 0;
    public int mercadologico2 = 0;
    public int mercadologico3 = 0;
    public int mercadologico4 = 0;
    public int mercadologico5 = 0;
    public int nivel = 0;
    public boolean cadastrado = true;
    public String strMercadologico1 = "";
    public String strMercadologico2 = "";
    public String strMercadologico3 = "";
    public String strMercadologico4 = "";
    public String strMercadologico5 = "";
    public List<MercadologicoVO> vMercadologico = new ArrayList();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Utils.acertarTexto(descricao, 35, "SEM DESCRICAO");
    }

    public int getMercadologico1() {
        return mercadologico1;
    }

    public void setMercadologico1(int mercadologico1) {
        this.mercadologico1 = mercadologico1;
    }

    public int getMercadologico2() {
        return mercadologico2;
    }

    public void setMercadologico2(int mercadologico2) {
        this.mercadologico2 = mercadologico2;
    }

    public int getMercadologico3() {
        return mercadologico3;
    }

    public void setMercadologico3(int mercadologico3) {
        this.mercadologico3 = mercadologico3;
    }

    public int getMercadologico4() {
        return mercadologico4;
    }

    public void setMercadologico4(int mercadologico4) {
        this.mercadologico4 = mercadologico4;
    }

    public int getMercadologico5() {
        return mercadologico5;
    }

    public void setMercadologico5(int mercadologico5) {
        this.mercadologico5 = mercadologico5;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel <= 0 ? nivel = 1 : nivel;
    }

    public boolean isCadastrado() {
        return cadastrado;
    }

    public void setCadastrado(boolean cadastrado) {
        this.cadastrado = cadastrado;
    }

    public List<MercadologicoVO> getvMercadologico() {
        return vMercadologico;
    }

    public void setvMercadologico(List<MercadologicoVO> vMercadologico) {
        this.vMercadologico = vMercadologico;
    }
    
    /**
     * Utilize esta função para obter a chave primária do mercadológico
     * @return Chave primária do mercadológico.
     */
    public String getChaveUnica() {
        return 
                String.valueOf(mercadologico1) + '-' + 
                String.valueOf(mercadologico2) + '-' + 
                String.valueOf(mercadologico3) + '-' + 
                String.valueOf(mercadologico4) + '-' + 
                String.valueOf(mercadologico5);
    }

    @Override
    public String toString() {
        return "MercadologicoVO{" + "id=" + id + ", descricao=" + descricao + ", mercadologico1=" + mercadologico1 + ", mercadologico2=" + mercadologico2 + ", mercadologico3=" + mercadologico3 + ", mercadologico4=" + mercadologico4 + ", mercadologico5=" + mercadologico5 + ", nivel=" + nivel + '}';
    }
    
    
    
}
