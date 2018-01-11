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
public class CompradorVO {

    public int id = 0;
    public String nome = "";
    public int id_situacaoCadastro = 0;
    public String situacaoCadastro = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId_situacaoCadastro() {
        return id_situacaoCadastro;
    }

    public void setId_situacaoCadastro(int id_situacaoCadastro) {
        this.id_situacaoCadastro = id_situacaoCadastro;
    }

    public String getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(String situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }
        
}
