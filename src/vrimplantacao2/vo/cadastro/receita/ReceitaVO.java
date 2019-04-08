/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.receita;

import vrimplantacao.utils.Utils;

/**
 *
 * @author lucasrafael
 */
public class ReceitaVO {

    private int id;
    private String descricao;
    private int id_situacaocadastro;
    private String fichatecnica;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = Utils.acertarTexto(descricao, 30);
    }

    /**
     * @return the id_situacaocadastro
     */
    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    /**
     * @param id_situacaocadastro the id_situacaocadastro to set
     */
    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    /**
     * @return the fichatecnica
     */
    public String getFichatecnica() {
        return fichatecnica;
    }

    /**
     * @param fichatecnica the fichatecnica to set
     */
    public void setFichatecnica(String fichatecnica) {
        this.fichatecnica = fichatecnica;
    }
}
