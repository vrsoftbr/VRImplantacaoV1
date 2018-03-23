/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.pdv.operador;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author lucasrafael
 */
public class OperadorVO {

    private int id = 0;
    private int id_loja = 1;
    private int matricula = 0;
    private String nome = "";
    private int senha = 0;
    private int codigo = 0;
    private int id_tiponiveloperador = 5;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome);
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getId_tiponiveloperador() {
        return id_tiponiveloperador;
    }

    public void setId_tiponiveloperador(int id_tiponiveloperador) {
        this.id_tiponiveloperador = id_tiponiveloperador;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

}
